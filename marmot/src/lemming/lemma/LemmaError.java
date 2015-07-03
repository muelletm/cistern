// Copyright 2015 Thomas Müller
// This file is part of MarMoT, which is licensed under GPLv3.

package lemming.lemma;

public class LemmaError {

		private LemmaInstance instance_;
		private String predicted_lemma_;
		private boolean oov_;

		public LemmaError(LemmaInstance instance, String predicted_lemma, boolean oov) {
			instance_ = instance;
			predicted_lemma_ = predicted_lemma;
			oov_ = oov;
		}

		public LemmaInstance getInstance() {
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

		public boolean isOOV() {
			return oov_;
		}

}
