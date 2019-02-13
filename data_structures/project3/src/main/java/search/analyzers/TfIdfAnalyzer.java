package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;
import java.net.URI;

/**
 * This class is responsible for computing how "relevant" any given document is
 * to a given search query.
 *
 * See the spec for more details.
 */
public class TfIdfAnalyzer {
    // This field must contain the IDF score for every single word in all
    // the documents.
    private IDictionary<String, Double> idfScores;
    private IDictionary<URI, Double> allDocumentNorms;

    // This field must contain the TF-IDF vector for each webpage you were given
    // in the constructor.
    //
    // We will use each webpage's page URI as a unique key.
    private IDictionary<URI, IDictionary<String, Double>> documentTfIdfVectors;

    // Feel free to add extra fields and helper methods.

    public TfIdfAnalyzer(ISet<Webpage> webpages) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //
        this.idfScores = this.computeIdfScores(webpages);
        this.documentTfIdfVectors = this.computeAllDocumentTfIdfVectors(webpages);
    }

    // Note: this method, strictly speaking, doesn't need to exist. However,
    // we've included it so we can add some unit tests to help verify that your
    // constructor correctly initializes your fields.
    public IDictionary<URI, IDictionary<String, Double>> getDocumentTfIdfVectors() {
        return this.documentTfIdfVectors;
    }

    /**
     * Return a dictionary mapping every single unique word found
     * in every single document to their IDF score.
     */
    private IDictionary<String, Double> computeIdfScores(ISet<Webpage> pages) {
        /*
         * idf score = (number of pages) / (number of pages this word is in)
         * 1. get all words from all pages
         * 2. get all unique words from this
         * 3. check if each word is in the page
         * 4. compute score
         */
        
        // chainedhashdict is more efficient than arraydict
        IDictionary<String, Double> rawIdfScores = new ChainedHashDictionary<>();
        for (Webpage page : pages) {
            // create a set to keep track of words already counted in this page
            ISet<String> wordFound = new ChainedHashSet<>();
            for (String word : page.getWords()) {
                // if it's a new word in this page, update the score
                if (!wordFound.contains(word)) {
                    if (rawIdfScores.containsKey(word)) {
                        rawIdfScores.put(word, rawIdfScores.get(word)+1);                        
                    } else {
                        rawIdfScores.put(word, 1.0);
                    }
                    wordFound.add(word);
                }
            }
        }
        // now we have raw scores so compute final result
        int numpages = pages.size();
        // create a new dictionary so we don't modify the same structure we're iterating over
        IDictionary<String, Double> finalIdfScores = new ChainedHashDictionary<>();
        for (KVPair<String, Double> score : rawIdfScores) {
            double value = score.getValue();
            // ln(0) is not defined
            if (value != 0) {
                value = Math.log(numpages / value);
            }
            finalIdfScores.put(score.getKey(), value);
        }
        return finalIdfScores;
    }

    /**
     * Returns a dictionary mapping every unique word found in the given list
     * to their term frequency (TF) score.
     *
     * The input list represents the words contained within a single document.
     */
    private IDictionary<String, Double> computeTfScores(IList<String> words) {
        // tf = (number of times this words occurs in a page) / (number of words in this page)
        IDictionary<String, Double> tfScores = new ChainedHashDictionary<>();
        for (String word : words) {
            // use hash dictionary to count word frequency
            if (tfScores.containsKey(word)) {
                tfScores.put(word, tfScores.get(word) + 1.0);
            } else {
                tfScores.put(word, 1.0);
            }
        }
        
        int numWords = words.size();
        IDictionary<String, Double> weightedTfScores = new ChainedHashDictionary<>();
        for (KVPair<String, Double> pair : tfScores) {
            weightedTfScores.put(pair.getKey(), pair.getValue() / numWords);
        }
        return weightedTfScores;
    }

    /**
     * See spec for more details on what this method should do.
     */
    private IDictionary<URI, IDictionary<String, Double>> computeAllDocumentTfIdfVectors(ISet<Webpage> pages) {
        IDictionary<URI, IDictionary<String, Double>> documentScores = new ChainedHashDictionary<>();
        // find norm of each page at the same time
        IDictionary<URI, Double> documentNorms = new ChainedHashDictionary<>();
        
        // for each page, compute scores by multiplying tf*idf scores for each word
        for (Webpage page : pages) {
            URI pageURI = page.getUri();
            IDictionary<String, Double> tfIdfScores = new ChainedHashDictionary<>();
            IDictionary<String, Double> tfScores = computeTfScores(page.getWords());
            
            double norm = 0.0;
            // calculate tf-idf as tf*idf
            for (KVPair<String, Double> pair : tfScores) {
                String key = pair.getKey();
                double score = pair.getValue() * idfScores.get(key);
                norm += score * score;
                tfIdfScores.put(key, score);
            }
            documentScores.put(pageURI, tfIdfScores);
            documentNorms.put(pageURI, Math.sqrt(norm));
        }
        this.allDocumentNorms = documentNorms;
        return documentScores;
    }

    /**
     * Returns the cosine similarity between the TF-IDF vector for the given query and the
     * URI's document.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public Double computeRelevance(IList<String> query, URI pageUri) {
        // get tf-idf vector for the document and for the query
        IDictionary<String, Double> documentVector = documentTfIdfVectors.get(pageUri);
        IDictionary<String, Double> queryVector = computeTfScores(query);
        
        // calculate result without running a bunch of extra loops
        double queryVectorNorm = 0.0;
        double numerator = 0.0;
        for (KVPair<String, Double> pair : queryVector) {
            String word = pair.getKey();
            double tf = pair.getValue();
            
            // weight each tf with the global idf (if idf doesn't contain it, give it an idf=0)
            double idf = idfScores.containsKey(word) ? idfScores.get(word) : 0;

            double wordScore = tf*idf;
            queryVectorNorm += wordScore * wordScore;

            // if documentVector doesn't contain the word, give it a score of 0
            double documentWordScore = documentVector.containsKey(word) ? documentVector.get(word) : 0;
            numerator += wordScore * documentWordScore;
        }

        // denominator = norm(queryVector) * norm(documentVector)
        double denominator = Math.sqrt(queryVectorNorm) * allDocumentNorms.get(pageUri);
        // if denominator is 0, return 0
        return denominator != 0 ? numerator / denominator : 0.0;
    }
}
