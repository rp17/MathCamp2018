package linkedQueue;

import java.util.concurrent.atomic.AtomicInteger;

public class CheckSumMCv6 {
	LinkedQueue<Integer> queue;
	private final int nElements, nProducers;
	private static Thread[] threadArray;
	private AtomicInteger putSums = new AtomicInteger(0);
	private SnapShotCheckSum[] snapshots;
	Thread m;
	private int index = 0;
	LinkedQueue.Node<Integer> travel;
	private int[] array;

	public CheckSumMCv6() {
		this.queue = new LinkedQueue<Integer>();
		this.nElements = 1;
		this.nProducers = 2;
		threadArray = new Thread[nProducers];
		snapshots = new SnapShotCheckSum[nProducers * nElements];
		array = new int[nProducers * nElements];
		m = new Thread(new Monitor());
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CheckSumMCv6 queueTest = new CheckSumMCv6();
		queueTest.test();
	}

	public void test() {
		try {
			for (int i = 0; i < nProducers; i++) {
				threadArray[i] = new Thread(new Producer());
				threadArray[i].start();
			}
			for (int i = 0; i < nProducers; i++) {
				threadArray[i].join();
			}
			int[] array = snapshots[0].getArray();
			int takeSum = 0;
			for (int i = 0; i < array.length; i++) {
				takeSum += array[i];
			}
			int putSum = putSums.get();
			if (putSum == takeSum)
				System.out.println("Correct");
			else
				System.out.println("Error");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	class Producer implements Runnable {
		public void run() {
			try {
				int sum = 0;
				for (int i = 0; i < nElements; i++) {
					Integer seed = (int) (Math.random() * 10 + 1);
					seed = xorShift(seed);
					queue.put(seed);
					sum += seed;
				}
				putSums.getAndAdd(sum);
				m.join();
				m.run();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	class Monitor implements Runnable {

		public void run() {
			try {
				System.out.println("Monitor is running");
				System.out.println("-------------------------------");
				if (index == 0) {
					travel = queue.getHead().next.get();
				} else {
					System.out.println("Preivous Item: " + travel.item);
					travel = travel.next.get();
					System.out.println("Current Item: " + travel.item);
				}
				array[index] = travel.item;
				SnapShotCheckSum element = new SnapShotCheckSum(
						System.nanoTime(), array);
				System.out.println(element);
				snapshots[index] = element;
				index++;
				System.out.println("Index: " + index);
				System.out.println("Currnet Node: " + travel);
				System.out.println("Array: "
						+ snapshots[index - 1].arrayToString());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static int xorShift(int y) {
		y ^= (y << 6);
		y ^= (y >>> 21);
		y ^= (y << 7);
		return y;
	}

	class SnapShotCheckSum {
		private final long time;
		private int[] array;

		public SnapShotCheckSum(long initTime, int[] initArray) {
			time = initTime;
			array = initArray;
		}

		public long getTime() {
			return time;
		}

		public int[] getArray() {
			return array;
		}

		public String arrayToString() {
			String toReturn = "[";
			for (int i = 0; i < array.length - 1; i++) {
				toReturn += array[i] + ", ";
			}
			toReturn += array[array.length - 1] + "]";
			return toReturn;
		}

		public String toString() {
			return "[" + time + ", " + arrayToString() + "]";
		}
	}
}
