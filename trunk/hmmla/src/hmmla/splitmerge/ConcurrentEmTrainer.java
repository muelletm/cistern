// Copyright 2014 Thomas Müller
// This file is part of HMMLA, which is licensed under GPLv3.

package hmmla.splitmerge;

import hmmla.hmm.HmmModel;
import hmmla.hmm.Model;
import hmmla.hmm.Statistics;
import hmmla.io.Sentence;
import hmmla.util.AbstractSPMDCallable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class ConcurrentEmTrainer extends EmTrainer {

	private ArrayList<Worker> workers_ = null;

	private class Worker extends AbstractSPMDCallable<Sentence, Double> {

		private SimpleEmTrainer trainer_;
		private Model model_;
		private boolean update_;

		public Worker() {
			trainer_ = new SimpleEmTrainer();
		}

		public void reset(Iterator<Sentence> iter, Model model,
				HmmModel normalizedStatistics, boolean update) {
			super.reset(iter, 0.0);
			update_ = update;
			if (update) {
				// Create a shallow copy of model an replace
				// statistics with a copy of statistics.
				model_ = new Model(model);
				Statistics statistics = new Statistics(model_.getTagTable()
						.size(), model_.getWordTable().size());
				model_.setStatistics(statistics);
			} else {
				model_ = model;
			}
			trainer_.reset(model_, normalizedStatistics);
		}

		@Override
		protected Double apply(Sentence sentence, Double out) {
			out += trainer_.estep(sentence, update_);
			return out;
		}

		public Statistics getStatistics() {
			return model_.getStatistics();
		}
	}

	public ConcurrentEmTrainer(int threadNumber) {
		workers_ = new ArrayList<Worker>(threadNumber);
		for (int i = 0; i < threadNumber; i++) {
			this.workers_.add(new Worker());
		}
	}

	@Override
	public double estep(Model model, HmmModel normalizedStatistics,
			Iterable<Sentence> reader, boolean update) {
		final Iterator<Sentence> pairIterator = reader.iterator();
		Iterator<Sentence> iterator = new Iterator<Sentence>() {

			@Override
			public boolean hasNext() {
				return pairIterator.hasNext();
			}

			@Override
			public Sentence next() {
				return pairIterator.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};

		for (Worker w : workers_) {
			w.reset(iterator, model, normalizedStatistics, update);
		}

		ExecutorService executorService = Executors.newFixedThreadPool(workers_
				.size());

		List<Future<Double>> results = null;
		try {
			results = executorService.invokeAll(workers_);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		double ll = 0.0;
		for (Future<Double> f : results) {
			try {
				ll += f.get();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
		}

		if (update) {
			Statistics statistics = model.getStatistics();
			statistics.setZero();
			for (Worker w : workers_) {
				statistics.add(w.getStatistics());
			}
		}
		executorService.shutdown();
		return ll;
	}
}
