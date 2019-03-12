package utils.misc;

import edu.stanford.nlp.ling.IndexedWord;

public class IndexWordWrapper {
  
	private IndexedWord word;
   
	public IndexWordWrapper(IndexedWord representativeCoreferenceNode) {
        this.word = representativeCoreferenceNode;
    }
    public IndexedWord get() {
        return this.word;
    }
    public void set(IndexedWord s) {
        this.word = s;
    }
}