import pandas
from gurobipy import *
from random import shuffle

####################################################
### part 1: start by choosing caterers for each meal
####################################################

def choose_caterers(N, business_req, caterer_costs):
    # choose the maximum possible of the cheapest caterer, then one of the state requirements and the remaining
    # as the last caterer
    caterer_meals = [0 for _ in caterer_costs]
    caterer_meals[0] = int(business_req * N)  # take 0.65 * N rounded down
    if len(caterer_meals) > 2:
        caterer_meals[2] = 1  # caterer C
    caterer_meals[1] = N - sum(caterer_meals)
    return caterer_meals

##################
# process the data
##################

def process_data(caterer_meals, caterer_costs, menus):
    """
        INPUT: Takes 1) a list of how many meals per caterer
                     2) the cost of each caterer
                     3) a list of strings representing files containing menus of CSV data

        OUTPUT: returns 1) a list of processed csv data read from input #3
                        2) an expanded list of which caterer serves which meal using input #1
                        3) a list containing lists for each caterer, where the inner list is a list of food items for
                            caterer x organized as follows: [protein items, carb items, veg items]
                        4) a list containing lists for each caterer, where the inner list stores
                            [number of protein items, number of carb items, number of veg items]
        """

    # expand the caterer meal schedule so we have one index per meal (eg. [3,4] -> [0, 0, 0, 1, 1, 1, 1])
    meals = []
    for i,x in enumerate(caterer_meals):
        meals += x * [i]

    # read data from each menu
    data = [pandas.read_csv(x) for x in menus]

    # encode data as a long list, keeping track of where protein starts, carbs start and veg starts
    #
    # subsection 1: make each food item into an object with attributes {name, QI score, caterer}
    class Food:
        def __init__(self, name, score, caterer):
            self.name = name
            self.qi = score
            self.caterer = caterer

    # create a Meta-list for all items stored as [proteins, carbs, fats] for each caterer
    all_items = [list() for _ in caterer_costs]
    # keep track of the amount of protein dishes, carb dishes and veg dishes for each caterer
    nutrient_counts = [[0, 0, 0] for _ in caterer_costs]

    # select all data points from caterers A and B
    for caterer, d in enumerate(data):
        # for each row in the data set, pick the item if it is a protein
        for i in range(len(d)):
            food_row = d.loc[i, :]
            if food_row[3] == 'Protein':
                # index 1 is name, 2 is score, 3 is category, 4 is optional
                new_food_ = Food(food_row[1], food_row[2], caterer)
                all_items[caterer].append(new_food_)  # add a Food object with information to the caterer's items
                nutrient_counts[caterer][0] += 1
        # pick carbs
        for i in range(len(d)):
            food_row = d.loc[i, :]
            if food_row[3] == 'Carbs':
                # index 1 is name, 2 is score, 3 is categ   ory, 4 is optional
                new_food_ = Food(food_row[1], food_row[2], caterer)
                all_items[caterer].append(new_food_)
                nutrient_counts[caterer][1] += 1
        # pick vegetables
        for i in range(len(d)):
            food_row = d.loc[i, :]
            if food_row[3] == 'Vegetables':
                # index 1 is name, 2 is score, 3 is category, 4 is optional
                new_food_ = Food(food_row[1], food_row[2], caterer)
                all_items[caterer].append(new_food_)
                nutrient_counts[caterer][2] += 1

    #
    # now we have a list of food objects like:
    # [protein1, protein2, ..., protein_i, carb1, carb2, carb3, ..., carb_j, veg1, veg2, ..., veg_k]
    # for each caterer. we also know the values of i,j and k. Each object has a name and a score.
    #
    return (data, meals, all_items, nutrient_counts)

######################################
## set up gurobi model and constraints
######################################

def solve_model(data, all_items, nutrient_counts, meals, skip_constraint=None):
    """
        INPUT: take raw data, parsed menu data, quantities of each type of dish each caterer has and the number of meals
                for each caterer, and the optional parameter skip_constraint for debugging/unit testing

        PROCESSING: set up and solve a linear programming problem with constraints
        OUTPUT: return a list containing models for each caterer and a list containing the decision variables for each caterer

    """
    # store results from each caterer
    models = []
    caterer_variables = []

    # solve an integer programming problem for each caterer given their meal-slots
    for caterer_ in range(len(data)):
        #####################################
        # create a new model for each caterer
        #####################################
        m = Model()
        decision_vars = []  # store decision variables
        number_of_items = len(all_items[caterer_])

        # add decision variables for food items from only this caterer
        for meal in meals:
            if meal == caterer_:
                # add a binary decision variable for each food item
                decision_vars.append([m.addVar(vtype=GRB.BINARY) for _ in all_items[meal]])
        m.update()

        # keep track of all variables and the model in global lists
        models.append(m)
        caterer_variables.append(decision_vars)

        ###################
        # add constraints
        ###################
        # n is the number of meals served by this caterer
        n = len(decision_vars)

        # fixed quantity for each caterer: number of protein, carb and veg dishes
        (protein_count,carb_count,vegetable_count) = nutrient_counts[caterer_]

        # for every day this caterer serves a meal,
        for d in range(n):
            # add constraints so that only 2 protein, 2 carb and 1 vegetable dish are chosen
            m.addConstr(sum(decision_vars[d][:protein_count]) == 2)
            m.addConstr(sum(decision_vars[d][protein_count:(protein_count+carb_count)]) == 2)
            m.addConstr(sum(decision_vars[d][(protein_count+carb_count):(protein_count+carb_count+vegetable_count)]) == 1)

        # variety of food in one month: only pick each dish at most 5 times in a month
        for i in range(number_of_items):
            # "fix" i, sum over each meal d, this is the number of times we use dish i from this caterer
            m.addConstr(sum(decision_vars[d][i] for d in range(n)) <= 2)

        # TODO: add variable encoding quality index minimum for certain days

        m.update()
        # print((d,x) for x in range(len(all_items[meals[d]])) for d in range(n))

        # try to maximize Quality Index of overall food chosen and minimize repetitions in choosing dishes
        m.setObjective(
            # sum(all_items[caterer_][x].qi * decision_vars[d][x] for x in range(number_of_items) for d in range(n)) , GRB.MAXIMIZE)
           sum(decision_vars[d][i] * all_items[caterer_][i].qi for d in range(n) for i in range(number_of_items)), GRB.MAXIMIZE)
        m.optimize()
    return (models, caterer_variables)


#######################################################
# Randomize caterer slots, meal-items from each caterer
#######################################################

def shuffle_meals(caterer_variables):
    """
        Take in a nested list of [:caterer [:day [:decision-variables]]]
        Return a list of [:day (caterer, [decision-variables])] with days reordered
    """
    # group chosen meals together, then shuffle
    # shuffle reorders items in the first layer, so food items are not mixed up in the process
    reordered_meals = [[c,meal] for c,caterer in enumerate(caterer_variables) for meal in caterer]
    shuffle(reordered_meals)
    return reordered_meals

####################
# print menu results
####################
def print_results(cvars, food_items, models, caterer_meals):
    """ Pretty print the results for anyone to be able to read which meals to pick and which caterers to pick on which day """
    d = 0
    # print('Results: (decision variable, QI, caterer, name)')
    # for caterer in range(len(data)):

    # get caterer and decision variables for each caterer-meal
    for c,variables in cvars:
        d += 1
        print('Meal', d)
        for i,var in enumerate(variables):
            # var.x is whether we use dish i in this meal
            if var.x == 1:
                print('\t%s, %s' % (food_items[c][i].name, food_items[c][i].qi))
                # print(var.x)
        print()
    print('Caterer schedule:', [['A', 'B', 'C'][i] for i,j in cvars])
    print('Objective function values:', models[0].ObjVal * 1.0 / caterer_meals[0],
                                        models[1].ObjVal * 1.0 / caterer_meals[1],
                                        models[2].ObjVal * 1.0 / caterer_meals[2])


#####################################
# MAIN
#####################################

N = 20
business_req = 0.65
menu_csv = ['foodQIA.csv', 'foodQIB.csv', 'foodQIC.csv']
caterer_costs = [32, 42, 50]

# 1. choose how many meals for each caterer
caterer_meals = choose_caterers(N, business_req, caterer_costs)

# 2. process data
(data, meals, all_items, nutrient_counts) = process_data(caterer_meals, caterer_costs, menu_csv)

# 3. set up and solve linear programming model
(models, caterer_variables) = solve_model(data, all_items, nutrient_counts, meals)

# 4. shuffle order of which caterer-menu to use on which day, default is first caterer, then second caterer
reordered_meals = shuffle_meals(caterer_variables)

# 5. print results
print_results(reordered_meals, all_items, models, caterer_meals)

# models,caterer_variables = solve_model(data, all_items, nutrient_counts, meals)
# reordered_meals = shuffle_meals(caterer_variables)
# print_results(reordered_meals, all_items, models)
