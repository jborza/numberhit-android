package eu.borzaindustries.numberhit;

import java.util.LinkedList;

public class PriorityQueue {
	private LinkedList<Btn> items;

	public PriorityQueue() {
		items = new LinkedList<Btn>();
	}

	public LinkedList<Btn> copyList() {
		LinkedList<Btn> copy = new LinkedList<Btn>(items);
		return copy;
	}

	public Btn pop() {
		if (size() == 0)
			return null;
		// find the largest one
		Btn biggest = items.peek();
		for (Btn b : items) {
			if (b.compareTo(biggest) > 0)
				biggest = b;
		}
		// remove it from the list
		items.remove(biggest);
		return biggest;
	}

	public void push(Btn c) {
		items.addLast(c);
	}

	public int size() {
		return items.size();
	}
}
