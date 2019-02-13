package calculator.ast;

import calculator.interpreter.Environment;
import calculator.errors.EvaluationError;
import datastructures.interfaces.IDictionary;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.dictionaries.ArrayDictionary;
import datastructures.interfaces.IList;

/**
 * All of the public static methods in this class are given the exact same parameters for
 * consistency. You can often ignore some of these parameters when implementing your
 * methods.
 *
 * Some of these methods should be recursive. You may want to consider using public-private
 * pairs in some cases.
 */
public class ExpressionManipulators {
    /**
     * Checks to make sure that the given node is an operation AstNode with the expected
     * name and number of children. Throws an EvaluationError otherwise.
     */
    private static void assertNodeMatches(AstNode node, String expectedName, int expectedNumChildren) {
        if (!node.isOperation()
                && !node.getName().equals(expectedName)
                && node.getChildren().size() != expectedNumChildren) {
            throw new EvaluationError("Node is not valid " + expectedName + " node.");
        }
    }

    /**
     * Accepts an 'toDouble(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'toDouble'.
     * - The 'node' parameter has exactly one child: the AstNode to convert into a double.
     *
     * Postconditions:
     *
     * - Returns a number AstNode containing the computed double.
     *
     * For example, if this method receives the AstNode corresponding to
     * 'toDouble(3 + 4)', this method should return the AstNode corresponding
     * to '7'.
     *
     * This method is required to handle the following binary operations
     *      +, -, *, /, ^
     *  (addition, subtraction, multiplication, division, and exponentiation, respectively)
     * and the following unary operations
     *      negate, sin, cos
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if any of the expressions uses an unknown operation.
     */
    public static AstNode handleToDouble(Environment env, AstNode node) {
        // To help you get started, we've implemented this method for you.
        // You should fill in the locations specified by "your code here"
        // in the 'toDoubleHelper' method.
        //
        // If you're not sure why we have a public method calling a private
        // recursive helper method, review your notes from CSE 143 (or the
        // equivalent class you took) about the 'public-private pair' pattern.

        assertNodeMatches(node, "toDouble", 1);
        AstNode exprToConvert = node.getChildren().get(0);
        return new AstNode(toDoubleHelper(env.getVariables(), exprToConvert));
    }

    private static double toDoubleHelper(IDictionary<String, AstNode> variables, AstNode node) {
        // There are three types of nodes, so we have three cases. 
        if (node.isNumber()) {
            return node.getNumericValue();
        } else if (node.isVariable()) {
            String name = node.getName();
            if (!variables.containsKey(name)) {
                // variable is not defined
                throw new EvaluationError("Node is not valid " + node + " node.");
            }
            // else: variable is defined
            // recurse with value of variable
            return toDoubleHelper(variables, variables.get(name));
        } else {
            String name = node.getName();

            // deal with single parameter functions
            if (name.equals("negate") || name.equals("sin") || name.equals("cos")) {
                double child = toDoubleHelper(variables, node.getChildren().get(0));
                if (name.equals("negate")) {
                    return -1 * child;
                } else if (name.equals("sin")) {
                    return Math.sin(child);
                } else if (name.equals("cos")) {
                    return Math.cos(child);
                }
            } else if (name.equals("+") || name.equals("-") || name.equals("*") || 
                    name.equals("/") || name.equals("^")) {
                // deal with multiple parameter functions
                // idea: evaluate recursively on each parameter, then evaluate this function
                return toDoubleChildReduce(variables, node);
                
            }
            // the operation is not defined, throw evaluation error
            throw new EvaluationError("Node is not valid " + node + " node.");
        }
    }
    
    // extra method to deal with reducing 
    private static double toDoubleChildReduce(IDictionary<String, AstNode> variables, AstNode node) {
        // use a reduce function starting with the first child
        double result = toDoubleHelper(variables, node.getChildren().get(0));
        for (AstNode child : node.getChildren()) {
            // skip child inside iterator because it's already been eval'd
            if (child == node.getChildren().get(0)) {
                continue;
            }
            double childValue = toDoubleHelper(variables, child);
            switch (node.getName()) {
                case "*": result *= childValue;
                    break;
                case "/": result /= childValue;
                    break;
                case "+": result += childValue;
                    break;
                case "-": result -= childValue;
                    break;
                case "^": result = Math.pow(result, childValue);
                    break;
                default: System.err.println("Unexpected exit of switch statement");
            }
        }
        return result;
    }

    /**
     * Accepts a 'simplify(inner)' AstNode and returns a new node containing the simplified version
     * of the 'inner' AstNode.
     *
     * Preconditions:
     *
     * - The 'node' parameter is an operation AstNode with the name 'simplify'.
     * - The 'node' parameter has exactly one child: the AstNode to simplify
     *
     * Postconditions:
     *
     * - Returns an AstNode containing the simplified inner parameter.
     *
     * For example, if we received the AstNode corresponding to the expression
     * "simplify(3 + 4)", you would return the AstNode corresponding to the
     * number "7".
     *
     * Note: there are many possible simplifications we could implement here,
     * but you are only required to implement a single one: constant folding.
     *
     * That is, whenever you see expressions of the form "NUM + NUM", or
     * "NUM - NUM", or "NUM * NUM", simplify them.
     */
    public static AstNode handleSimplify(Environment env, AstNode node) {
        // similar boilerplate to handleDouble
        // check that the first node is a simplify node
        assertNodeMatches(node, "simplify", 1);
        AstNode exprToConvert = node.getChildren().get(0);
        return simplifyHelper(env.getVariables(), exprToConvert);
    }

    // Only simplifies expressions of the form NUM + NUM, NUM - NUM, NUM * NUM
    private static AstNode simplifyHelper(IDictionary<String, AstNode> variables, AstNode node) {
        // deal with variable case first -- if variable, get value
        // if undefined variable, don't simplify
        if (node.isVariable() && variables.containsKey(node.getName())) {
            return simplifyHelper(variables, variables.get(node.getName()));
        }
        
        // if operation, check if +,-,* otherwise recurse for each of the children
        if (node.isOperation()) {
            String name = node.getName();
            IList<AstNode> children = node.getChildren();
            if ((name.equals("+") || name.equals("*") || name.equals("-")) && node.getChildren().size() == 2) {
                AstNode result = simplifyHelperHelper(variables, node);
                if (result != null) {
                    // gross workaround since "method is too large"
                    return result;
                }
            }
            // efficient for-loop with counter to update children
            int i = 0;
            for (AstNode child : children) {
                AstNode updatedChild = simplifyHelper(variables, child);
                // if there's no difference, don't replace the child; this will save time
                if (updatedChild != child) {
                    children.set(i, updatedChild);
                }
                i++;
            }
        }
        return node;
    }
    
    private static AstNode simplifyHelperHelper(IDictionary<String, AstNode> variables, AstNode node) {
        // ideas:
        // * elements to be replaced can only be next to each other and not variables
        // * there can only be two children to any operation; 
        //     as per the spec we don't care about any spare children
        IList<AstNode> children = node.getChildren();
        AstNode firstChild = children.get(0);
        AstNode secondChild = children.get(1);
        
        // recursively resolve further +,-,* functions
        if (firstChild.isOperation()) {
            firstChild = simplifyHelper(variables, firstChild);
        }
        if (secondChild.isOperation()) {
            secondChild = simplifyHelper(variables, secondChild);
        }
        
        // check for variables
        if (firstChild.isVariable() && variables.containsKey(firstChild.getName())) {
            firstChild = simplifyHelper(variables, variables.get(firstChild.getName()));
        }
        if (secondChild.isVariable() && variables.containsKey(secondChild.getName())) {
            secondChild = simplifyHelper(variables, variables.get(secondChild.getName()));
        }
        
        // we should have gotten two numbers out of child reduction; ignore otherwise
        if (firstChild.isNumber() && secondChild.isNumber()) {
            int result = 0;
            int firstInt = (int) firstChild.getNumericValue();  // convert to integer
            int secondInt = (int) secondChild.getNumericValue();
            
            String name = node.getName();
            if (name.equals("+")) {
                result = firstInt + secondInt;
            } else if (name.equals("-")) {
                result = firstInt - secondInt;
            } else if (name.equals("*")) {
                result = firstInt * secondInt;
            }
            // replace current node with a new node of updated value
            return new AstNode(result);
        }
        return null;
    }

    /**
     * Accepts an Environment variable and a 'plot(exprToPlot, var, varMin, varMax, step)'
     * AstNode and generates the corresponding plot on the ImageDrawer attached to the
     * environment. Returns some arbitrary AstNode.
     *
     * Example 1:
     *
     * >>> plot(3 * x, x, 2, 5, 0.5)
     *
     * This method will receive the AstNode corresponding to 'plot(3 * x, x, 2, 5, 0.5)'.
     * Your 'handlePlot' method is then responsible for plotting the equation
     * "3 * x", varying "x" from 2 to 5 in increments of 0.5.
     *
     * In this case, this means you'll be plotting the following points:
     *
     * [(2, 6), (2.5, 7.5), (3, 9), (3.5, 10.5), (4, 12), (4.5, 13.5), (5, 15)]
     *
     * ---
     *
     * Another example: now, we're plotting the quadratic equation "a^2 + 4a + 4"
     * from -10 to 10 in 0.01 increments. In this case, "a" is our "x" variable.
     *
     * >>> c := 4
     * 4
     * >>> step := 0.01
     * 0.01
     * >>> plot(a^2 + c*a + a, a, -10, 10, step)
     *
     * ---
     *
     * @throws EvaluationError  if any of the expressions contains an undefined variable.
     * @throws EvaluationError  if varMin > varMax
     * @throws EvaluationError  if 'var' was already defined
     * @throws EvaluationError  if 'step' is zero or negative
     */
    public static AstNode plot(Environment env, AstNode node) {
        assertNodeMatches(node, "plot", 5);

        IList<AstNode> children = node.getChildren();

        // Get variables
        IDictionary<String, AstNode> variables = env.getVariables();        
        
        // Get number for begin and end -- stepper starts as varMin
        double stepper = toDoubleHelper(variables, children.get(2));
        double varMax = toDoubleHelper(variables, children.get(3));
        
        // Max should not be smaller than min
        if (stepper > varMax) {
        	throw new EvaluationError("Max is less than Min");
        }
        
        AstNode expr = children.get(0);
        AstNode var = children.get(1);
        
        // Variable should not be defined
        if (variables.containsKey(var.getName())) {
        	throw new EvaluationError("Variable was already defined");
        }
        
        // Get step value
        double step = toDoubleHelper(variables, children.get(4));
        if (step <= 0) {
        	throw new EvaluationError("Step is <= 0");
        }
        
        // Create lists to store points to plot
        IList<Double> xVals = new DoubleLinkedList<>();
        IList<Double> yVals = new DoubleLinkedList<>();
        
        // attempt to simplify expression for optimization purposes
        // (if something breaks, maybe fix this)
        expr = simplifyHelper(variables, expr);
        
        // step through and generate list
        while (stepper <= varMax) {
        	xVals.add(stepper);   // add x point
        	// update variable value
        	variables.put(var.getName(), new AstNode(stepper));
        	yVals.add(toDoubleHelper(variables, expr));   // add y point
        	stepper += step;
        }
        
        // note: graph name is left empty intentionally (replace if needed)
        env.getImageDrawer().drawScatterPlot("", "x axis", "y axis", xVals, yVals);
        variables.remove(var.getName());
        
        // Note: every single function we add MUST return an
        // AST node that your "simplify" function is capable of handling.
        // However, your "simplify" function doesn't really know what to do
        // with "plot" functions (and what is the "plot" function supposed to
        // evaluate to anyways?) so we'll settle for just returning an
        // arbitrary number.
        //
        // When working on this method, you should uncomment the following line:
        //
        return new AstNode(1);
    }
    
    /*** EXTRA CREDIT: solver function ***/
    // TODO: fix comment v
    // @throws exceptions if it is not passed an equals AstNode with exactly two children
    public static AstNode handleSolve(Environment env, AstNode node) {
        // similar boilerplate to other handle methods
        // check that the first node is a solve node
        assertNodeMatches(node, "solve", 2);
        AstNode exprToConvert = node.getChildren().get(0);
        AstNode varToConvert = node.getChildren().get(1);
        // x is the initial guess passed in
        // tolerance is how close the result should be to the actual solution
        // dx is the amount to step by (to find a finer solution)
        // timeout is the maximum number of steps before a solution is not found
        return solveHelper(env.getVariables(), exprToConvert, varToConvert, 0, 0.01, 1, 10000);
    }
    
    /**
     *  pre: takes a IDictionary of defined variables
     *            an AstNode of the expression to solve
     *            an AstNode for the variable to solve for
     *            an initial guess
     *            an error tolerance (lower is better)
     *            a step value to take differentials (0.3 <= dx <= 1 is good)
     *            a timeout to stop if a solution can not be found
     *            
     * post: returns an AstNode with the solution if found
     *       if not found, returns an AstNode with null value
     *       @throws an EvaluationError if the variable to solve for was already defined
     */
    private static AstNode solveHelper(IDictionary<String, AstNode> variables, AstNode node, AstNode var,
                double x, double tolerance, double dx, int timeout) {
        /*
         * Strategy: 
         *     convert (= a b) into (= (- a b) 0)
         *     choose a starting value for x (x = 0)
         *     choose a tolerance and dx
         *     compute f(x) and f'(x)
         *     use Newton's method to solve for x
         */
        assertNodeMatches(node, "=", 2);
        String varName = var.getName();
        if (variables.containsKey(varName)) {
            throw new EvaluationError("Variable was already defined");
        }
        
        // convert node into a difference node with left and right children
        // Note that simplifyHelper will take care of any extra variables that are defined
        AstNode equationToSolve = simplifyHelper(variables, new AstNode("-", node.getChildren()));
        
        // Other undefined variables should cause an error when doubleHelper is called
        // We are going to use this behavior to optimize our code
        double f;  // f(x)
        double df; // f(x+dx)

        // define new "variable" dictionaries so that updating is fast
        IDictionary<String, AstNode> xMapping = new ArrayDictionary<>();
        IDictionary<String, AstNode> dxMapping = new ArrayDictionary<>();
        
        // get initial function value
        xMapping.put(varName, new AstNode(x));
        f = toDoubleHelper(xMapping, equationToSolve);
        
        // must check for infinite loop, eg. sin(x) = 5
        int counter = 0;
        while (Math.abs(f) > tolerance && counter < timeout) {
            // update variable values
            xMapping.put(varName, new AstNode(x));
            dxMapping.put(varName, new AstNode(x + dx));
            // get function values
            f = toDoubleHelper(xMapping, equationToSolve);
            df = toDoubleHelper(dxMapping, equationToSolve);
            // update x using newton's method
            x = x - (f / (df - f));
            counter++;
        }
        
        // timed out, return null
        if (counter == timeout) {
            return new AstNode(null);
        }
        return new AstNode(x);
    }
}
