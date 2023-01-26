package me.fopzl.doctor.util.tuples;

import java.util.Collection;
import java.util.Iterator;

import me.fopzl.doctor.util.tuples.value.IValue0;
import me.fopzl.doctor.util.tuples.value.IValue1;
import me.fopzl.doctor.util.tuples.value.IValue2;

public final class Triplet<A,B,C>
extends Tuple
implements IValue0<A>,
IValue1<B>,
IValue2<C> {

	private static final long serialVersionUID = -1877265551599483740L;

	private static final int SIZE = 3;

	private final A val0;
	private final B val1;
	private final C val2;

	public static <A,B,C> Triplet<A,B,C> with(final A value0, final B value1, final C value2) {
		return new Triplet<A,B,C>(value0,value1,value2);
	}

	public static <X> Triplet<X,X,X> fromArray(final X[] array) {
		if (array == null) {
			throw new IllegalArgumentException("Array cannot be null");
		}
		if (array.length != 3) {
			throw new IllegalArgumentException("Array must have exactly 3 elements in order to create a Triplet. Size is " + array.length);
		}
		return new Triplet<X,X,X>(array[0],array[1],array[2]);
	}

	public static <X> Triplet<X,X,X> fromCollection(final Collection<X> collection) {
		return fromIterable(collection);
	}

	public static <X> Triplet<X,X,X> fromIterable(final Iterable<X> iterable) {
		return fromIterable(iterable, 0, true);
	}

	public static <X> Triplet<X,X,X> fromIterable(final Iterable<X> iterable, int index) {
		return fromIterable(iterable, index, false);
	}

	private static <X> Triplet<X,X,X> fromIterable(final Iterable<X> iterable, int index, final boolean exactSize) {
		if (iterable == null) {
			throw new IllegalArgumentException("Iterable cannot be null");
		}

		boolean tooFewElements = false;

		X element0 = null;
		X element1 = null;
		X element2 = null;

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

		if (tooFewElements && exactSize) {
			throw new IllegalArgumentException("Not enough elements for creating a Triplet (3 needed)");
		}

		if (iter.hasNext() && exactSize) {
			throw new IllegalArgumentException("Iterable must have exactly 3 available elements in order to create a Triplet.");
		}

		return new Triplet<X,X,X>(element0, element1, element2);

	}

	public Triplet(
			final A value0,
			final B value1,
			final C value2) {
		super(value0, value1, value2);
		this.val0 = value0;
		this.val1 = value1;
		this.val2 = value2;
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
	public int getSize() {
		return SIZE;
	}
}
