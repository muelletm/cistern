package marmot.lemma.toutanova;

public interface Decoder {

	public Result decode(ToutanovaInstance instance);

	public void init(Model model_);

	public int getOrder();

}
