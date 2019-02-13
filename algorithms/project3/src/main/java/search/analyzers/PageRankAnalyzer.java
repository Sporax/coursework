package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing the 'page rank' of all available webpages.
 * If a webpage has many different links to it, it should have a higher page rank.
 * See the spec for more details.
 */
public class PageRankAnalyzer {
    private IDictionary<URI, Double> pageRanks;

    /**
     * Computes a graph representing the internet and computes the page rank of all
     * available webpages.
     *
     * @param webpages  A set of all webpages we have parsed.
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    public PageRankAnalyzer(ISet<Webpage> webpages, double decay, double epsilon, int limit) {
        // Make a graph representing the "internet"
        IDictionary<URI, ISet<URI>> graph = this.makeGraph(webpages);

        // Use this graph to compute the page rank for each webpage
        this.pageRanks = this.makePageRanks(graph, decay, limit, epsilon);

        // Note: we don't store the graph as a field: once we've computed the page ranks, we no longer need it
    }

    /**
     * This method converts a set of webpages into an unweighted, directed graph,
     * in adjacency list form.
     *
     * You may assume that each webpage can be uniquely identified by its URI.
     *
     * Note that a webpage may contain links to other webpages that are *not*
     * included within set of webpages you were given. You should omit these
     * links from your graph: we want the final graph we build to be
     * entirely "self-contained".
     */
    private IDictionary<URI, ISet<URI>> makeGraph(ISet<Webpage> webpages) {
        // 1. page should not map to itself
        // 2. if a page links to something that is not a page, ignore it
        IDictionary<URI, ISet<URI>> graph = new ChainedHashDictionary<>();
        
        // get a listing of all URIs
        ISet<URI> allURIs = new ChainedHashSet<>();
        for (Webpage page : webpages) {
            allURIs.add(page.getUri());   // only unique URIs will be stored
        }
        
        for (Webpage page : webpages) {
            // for each page, get page links
            // for each link that is not itself and is a valid URI, add to mapping
            URI pageName = page.getUri();
            // create a new set if one doesn't exist already  (we are not guaranteed unique webpages)
            ISet<URI> pageLinks = new ChainedHashSet<>();
            if (graph.containsKey(pageName)) {
                pageLinks = graph.get(pageName);
            }
            for (URI link : page.getLinks()) {
                if (link != null && !link.equals(pageName) && allURIs.contains(link)) {
                    pageLinks.add(link);
                }
            }
            graph.put(pageName, pageLinks);
        }
        return graph;
    }

    /**
     * Computes the page ranks for all webpages in the graph.
     *
     * Precondition: assumes 'this.graphs' has previously been initialized.
     *
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less then or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    private IDictionary<URI, Double> makePageRanks(IDictionary<URI, ISet<URI>> graph,
                                                   double decay,
                                                   int limit,
                                                   double epsilon) {
        // create two dictionaries to store old and new ranks
        IDictionary<URI, Double> oldRanks = new ChainedHashDictionary<>();
        IDictionary<URI, Double> newRanks = new ChainedHashDictionary<>();
        // initialize values: oldRanks = (1/n) at the beginning and newRanks = 0
        double n = graph.size();
        for (KVPair<URI, ISet<URI>> pair : graph) {  // initialize loop
            URI vertex = pair.getKey();
            oldRanks.put(vertex, 1.0/n);
            newRanks.put(vertex, 0.0);
        }
        
        // loop until convergence
        for (int i = 0; i < limit; i++) {
            // childlessWeight represents the amount we need to add to each node at the end (for those without children)
            double childlessWeight = 0;
            // loop over all vertices -- progress loop
            for (KVPair<URI, ISet<URI>> pair : graph) {
                URI vertex = pair.getKey();
                ISet<URI> children = pair.getValue();
                double oldRank = oldRanks.get(vertex);

                // if no children, don't divide by 0
                int numChildren = children.size();
                if (numChildren == 0) {
                    childlessWeight += oldRank;  // add weight so we can distribute at the end
                    numChildren = 1;
                }
                
                // update each child by d*(old rank)/(numChildren)
                double newValue = decay * oldRank / numChildren;
                // if no children, the following loop will be skipped
                for (URI child : children) {
                    newRanks.put(child, newRanks.get(child) + newValue);
                }
            }
            // check if we have converged -- check & update loop
            boolean converged = true;
            for (KVPair<URI, ISet<URI>> pair : graph) {
                URI vertex = pair.getKey();
                
                // update current vertex by (1-d)/n + childlessValue/n
                double newValue = newRanks.get(vertex) + (1 - decay + childlessWeight * decay) / n;
                double oldValue = oldRanks.get(vertex);

                // update oldRanks and wipe newRanks
                oldRanks.put(vertex, newValue);
                newRanks.put(vertex, 0.0);

                // condition to stop is (difference <= epsilon) for all differences
                if (converged && Math.abs(newValue - oldValue) > epsilon) {
                    converged = false;
                }
            }
            // Return early if we've converged
            if (converged) {
                break;  // we're going to return oldRanks anyway so this works
            }
        }
        return oldRanks;
    }

    /**
     * Returns the page rank of the given URI.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public double computePageRank(URI pageUri) {
        // Implementation note: this method should be very simple: just one line!
        return pageRanks.get(pageUri);
    }
}
