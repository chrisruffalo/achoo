package com.achoo.topicstore.trie;

import gnu.trove.map.hash.THashMap;

import java.util.Map;

import com.googlecode.javaewah32.EWAHCompressedBitmap32;

public class SharedMarker {

	private static final int PARTITION_WIDTH = Character.MAX_CODE_POINT;
	
	// the -32 is because the compressed bitmap leaves enough
	// space at the end for a single integer word and we're
	// emulating that behavior here and we're also clamping
	// it so that the BOUNDARY size is aligned perfectly
	// with the size of the PARTITION
	private static final int BOUNDARY = (((Integer.MAX_VALUE - 32)) / SharedMarker.PARTITION_WIDTH) * SharedMarker.PARTITION_WIDTH;
	
	private Map<Long, EWAHCompressedBitmap32> markers;
	
	public SharedMarker() {
		this.markers = new THashMap<>();
	}
	
	public void set(long position) {
		this.manipulate(position, true);
	}
	
	public boolean get(long position) {
		// calculate
		long partition = position / SharedMarker.BOUNDARY;
		int remainder = (int)(position % SharedMarker.BOUNDARY);
		
		// get partition
		EWAHCompressedBitmap32 bitmap = this.map(partition);

		// return value
		return bitmap.get(remainder);
	}
	
	public void unset(long position) {
		this.manipulate(position, false);
	}
	
	private void manipulate(long position, boolean set) {
		// calculate
		long partition = position / SharedMarker.BOUNDARY;
		int remainder = (int)(position % SharedMarker.BOUNDARY);
		
		// get partition
		EWAHCompressedBitmap32 bitmap = this.map(partition);
		
		// create aligned bit set
		EWAHCompressedBitmap32 manipulate = new EWAHCompressedBitmap32();
		manipulate.set(remainder);
		
		// manipulate according to logic
		if(set) {
			bitmap = bitmap.or(manipulate);
		} else {
			bitmap = bitmap.andNot(manipulate);
		}		
		
		// reset item in map
		this.markers.remove(partition);
		this.markers.put(partition, bitmap);
	}

	private EWAHCompressedBitmap32 map(long partition) {
		EWAHCompressedBitmap32 bitmap = this.markers.get(partition);
		if(bitmap == null) {
			bitmap = new EWAHCompressedBitmap32();
			this.markers.put(partition, bitmap);
		}
		return bitmap;
	}
	
	public long[] positions(long start) {
		// clamp start to nearest actual start
		start = ((long)(start / SharedMarker.PARTITION_WIDTH)) * ((long)SharedMarker.PARTITION_WIDTH);
		
		// basic calculation
		long partition = start / SharedMarker.BOUNDARY;

		// find the place in the partition that the start happens
		int begin = (int)(start - ((long)partition * (long)SharedMarker.BOUNDARY));
		int end = begin + SharedMarker.PARTITION_WIDTH;
		
		return this.positions(partition, start, begin, end);
	}
	
	private long[] positions(long partition, long base, int start, int stop) {
		// get the relevant slice
		EWAHCompressedBitmap32 set = this.slice(partition, start, stop);
		
		// copy results into an array and 
		// add the base offset
		int[] found = set.toArray();
		long[] result = new long[found.length];
		int i = 0;
		for(int f : found) {
			result[i++] = ((long)f) + base - start;
		}
		
		return result;
	}
	
	public int size(long start) {
		// clamp start to nearest actual start
		start = ((long)(start / SharedMarker.PARTITION_WIDTH)) * ((long)SharedMarker.PARTITION_WIDTH);
		
		// basic calculation
		long partition = start / SharedMarker.BOUNDARY;

		// find the place in the partition that the start happens
		int begin = (int)(start - ((long)partition * (long)SharedMarker.BOUNDARY));
		int end = begin + SharedMarker.PARTITION_WIDTH;

		return this.slice(partition, begin, end).cardinality();
	}
	
	private EWAHCompressedBitmap32 slice(long partition, int start, int stop) {
		EWAHCompressedBitmap32 bitmap = this.map(partition);

		// mask off the lower bits
		EWAHCompressedBitmap32 lowerMask = new EWAHCompressedBitmap32();
		lowerMask.setSizeInBits(start, true);
		
		//  mask off the upper bits
		EWAHCompressedBitmap32 upperMask = new EWAHCompressedBitmap32();
		upperMask.setSizeInBits(stop, true);
		upperMask = upperMask.andNot(lowerMask);
		
		// create a copy of the input bitmask and mask off the upper
		// and lower portions
		EWAHCompressedBitmap32 set = bitmap.and(upperMask);
		return set;
	}
	
	public void clear(long size) {
		
	}
}

