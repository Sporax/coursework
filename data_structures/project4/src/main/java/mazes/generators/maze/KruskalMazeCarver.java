package mazes.generators.maze;

import java.util.Random;
import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Room;
import mazes.entities.Wall;
import misc.graphs.Graph;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    @Override
    public ISet<Wall> returnWallsToRemove(Maze maze) {
        // Note: make sure that the input maze remains unmodified after this method is over.
        //
        // In particular, if you call 'wall.setDistance()' at any point, make sure to
        // call 'wall.resetDistanceToOriginal()' on the same wall before returning.
        
        Random rand = new Random();
        
        // Give walls random weights
        ISet<Wall> walls = maze.getWalls();
        for (Wall wall : walls) {
            wall.setDistance(rand.nextDouble());
        }

        // Make graph of room
        Graph<Room, Wall> mazeGraph = new Graph<>(maze.getRooms(), walls);
        
        // Get MST of the maze with random wall values
        ISet<Wall> wallsToRemove = mazeGraph.findMinimumSpanningTree();
        
        // Reset wall weights to original values
        for (Wall wall : walls) {
            wall.resetDistanceToOriginal();
        }

        return wallsToRemove;
    }
}
