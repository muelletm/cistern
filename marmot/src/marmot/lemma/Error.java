// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package marmot.lemma;

public class Error {

		private Instance instance_;
		private String predicted_lemma_;

		public Error(Instance instance, String predicted_lemma) {
			instance_ = instance;
			predicted_lemma_ = predicted_lemma;
		}

		public Instance getInstance() {
			return instance_;
		}

		public String getPredictedLemma() {
			return predicted_lemma_;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(instance_.getForm());
			sb.append(" ");
			if (instance_.getPosTag() != null) {
				sb.append(instance_.getPosTag());
				sb.append(" ");
			}
			sb.append(instance_.getLemma());
			sb.append(" (");
			sb.append(predicted_lemma_);
			sb.append(")");
			return sb.toString();
		}

}
