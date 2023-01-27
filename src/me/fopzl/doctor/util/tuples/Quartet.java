package me.fopzl.doctor.util.tuples;

import java.util.Collection;
import java.util.Iterator;

import me.fopzl.doctor.util.tuples.value.IValue0;
import me.fopzl.doctor.util.tuples.value.IValue1;
import me.fopzl.doctor.util.tuples.value.IValue2;
import me.fopzl.doctor.util.tuples.value.IValue3;

public final class Quartet<A, B, C, D> extends Tuple implements IValue0<A>, IValue1<B>, IValue2<C>, IValue3<D> {
	
	private static final long serialVersionUID = 2445136048617019549L;
	
	private static final int SIZE = 4;
	
	private final A val0;
	private final B val1;
	private final C val2;
	private final D val3;
	
	public static <A, B, C, D> Quartet<A, B, C, D> with(final A value0, final B value1, final C value2, final D value3) {
		return new Quartet<A, B, C, D>(value0, value1, value2, value3);
	}
	
	public static <X> Quartet<X, X, X, X> fromArray(final X[] array) {
		if (array == null) {
			throw new IllegalArgumentException("Array cannot be null");
		}
		if (array.length != 4) {
			throw new IllegalArgumentException("Array must have exactly 4 elements in order to create a Quartet. Size is " + array.length);
		}
		return new Quartet<X, X, X, X>(array[0], array[1], array[2], array[3]);
	}
	
	public static <X> Quartet<X, X, X, X> fromCollection(final Collection<X> collection) {
		return fromIterable(collection);
	}
	
	public static <X> Quartet<X, X, X, X> fromIterable(final Iterable<X> iterable) {
		return fromIterable(iterable, 0, true);
	}
	
	public static <X> Quartet<X, X, X, X> fromIterable(final Iterable<X> iterable, int index) {
		return fromIterable(iterable, index, false);
	}
	
	private static <X> Quartet<X, X, X, X> fromIterable(final Iterable<X> iterable, int index, final boolean exactSize) {
		
		if (iterable == null) {
			throw new IllegalArgumentException("Iterable cannot be null");
		}
		
		boolean tooFewElements = false;
		
		X element0 = null;
		X element1 = null;
		X element2 = null;
		X element3 = null;
		
		final Iterator<X> iter = iterable.iterator();
		
		int i = 0;
		while (i < index) {
			if (iter.hasNext()) {
				iter.next();
			} else {
				tooFewElements = true;
			}
			i++;
		}
		
		if (iter.hasNext()) {
			element0 = iter.next();
		} else {
			tooFewElements = true;
		}
		
		if (iter.hasNext()) {
			element1 = iter.next();
		} else {
			tooFewElements = true;
		}
		
		if (iter.hasNext()) {
			element2 = iter.next();
		} else {
			tooFewElements = true;
		}
		
		if (iter.hasNext()) {
			element3 = iter.next();
		} else {
			tooFewElements = true;
		}
		
		if (tooFewElements && exactSize) {
			throw new IllegalArgumentException("Not enough elements for creating a Quartet (4 needed)");
		}
		
		if (iter.hasNext() && exactSize) {
			throw new IllegalArgumentException("Iterable must have exactly 4 available elements in order to create a Quartet.");
		}
		
		return new Quartet<X, X, X, X>(element0, element1, element2, element3);
		
	}
	
	public Quartet(final A value0, final B value1, final C value2, final D value3) {
		super(value0, value1, value2, value3);
		this.val0 = value0;
		this.val1 = value1;
		this.val2 = value2;
		this.val3 = value3;
	}
	
	@Override
	public A getValue0() {
		return this.val0;
	}
	
	@Override
	public B getValue1() {
		return this.val1;
	}
	
	@Override
	public C getValue2() {
		return this.val2;
	}
	
	@Override
	public D getValue3() {
		return this.val3;
	}
	
	@Override
	public int getSize() {
		return SIZE;
	}
}
