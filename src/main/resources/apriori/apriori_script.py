#!/usr/bin/env python
# coding: utf-8

# ### Import Libraries

# In[1]:


import pandas as pd
import os


# ### Load Dataset

# In[2]:

file_path = os.path.dirname(os.path.realpath(__file__))
df = pd.read_csv(os.path.join(file_path,'./dataset/ratings.csv'))


# ### Create a new column which is 'favorable' from dataframe if user's rating > 3.0

# In[3]:


df['favorable'] = df['rating'] > 3.0


# ### Sample the dataset to form a training dataset 

# In[4]:


ratings = df[df['userId'].isin(range(200))] # 200 is ideal range otherwise calculating is so slow and hard


# ### Create dataset of only the favorable reviews 

# In[5]:


favorable_ratings = ratings[ratings['favorable']]


# ### Compute for each user's favorable by grouping the dataset by the userId 

# In[6]:


favorable_reviews_by_users = dict((k, frozenset(v.values)) for k, v in favorable_ratings.groupby('userId')['movieId'])


# ### Create a DataFrame that tells us how frequently each movie has been given a favorable review 

# In[7]:


num_favorable_by_movie = ratings[['movieId', 'favorable']].groupby('movieId').sum()


# ### Store our discovered frequent itemsets in a dictionary 

# In[9]:


frequent_itemsets = {}


# ### Define minimum support needed for an itemset to be considered frequent 

# In[10]:


min_support = 50


# ### Create an itemset with each movie individually and test if the itemset is frequent 

# In[11]:


frequent_itemsets[1] = dict((frozenset((movie_id,)), row["favorable"]) for movie_id, row in num_favorable_by_movie.iterrows() if row["favorable"] > min_support)


# ###  Define function that takes the newly discovered frequent itemsets

# In[12]:


from collections import defaultdict
def find_frequent_itemsets(favorable_reviews_by_users, k_1_itemsets, min_support):
    counts = defaultdict(int)
    # Iterate over all of the users and their reviews 
    for user, reviews in favorable_reviews_by_users.items():
        for itemset in k_1_itemsets:
            if itemset.issubset(reviews):
                for other_reviewed_movie in reviews - itemset:
                    current_superset = itemset | frozenset((other_reviewed_movie,))
                    counts[current_superset] += 1
    # the candidate itemsets have enough support to be considered frequent and return only those
    return dict([(itemset, frequency) for itemset, frequency in counts.items() if frequency >= min_support])


# ### Create a loop that iterates over the steps of the Apriori algorithm, storing the new itemsets

# In[13]:


# create the frequent itemsets and store them in dictionary by their length
for k in range(2, 20):
    cur_frequent_itemsets = find_frequent_itemsets(favorable_reviews_by_users, frequent_itemsets[k-1], min_support)
    frequent_itemsets[k] = cur_frequent_itemsets
    # break out the preceding loop if loop didn't find any new frequent itemsets
    if len(cur_frequent_itemsets) == 0:
#         print("Did not find any frequent itemsets of length {}".format(k))
        break

# ###  Make an association rule from a frequent itemset by taking one of the movies in the itemset and denoting it as the conclusion

# In[17]:


# first generate a list of all of the rules from each of the frequent itemsets, by iterating over each of the discovered frequent itemsets of each length
candidate_rules = []
for itemset_length, itemset_counts in frequent_itemsets.items():
    for itemset in itemset_counts.keys():
        # iterate over every movie in this itemset, using it as conclusion.The remaining movies in the itemset are the premise.
        for conclusion in itemset:
            premise = itemset - set((conclusion,))
            candidate_rules.append((premise, conclusion))


# ###  Compute the confidence of each of these rules

# In[18]:


correct_counts = defaultdict(int) # how many times we see the premise leading to the conclusion (a correct example of the rule)
incorrect_counts = defaultdict(int) # how many times it doesn't (an incorrect example)


# ### Iterate over all of the users, their favorable reviews, and over each candidate association rule 

# In[19]:


for user, reviews in favorable_reviews_by_users.items():
    for candidate_rule in candidate_rules:
        premise, conclusion = candidate_rule
        # Did the user favorably review all of the movies in the premise?
        if premise.issubset(reviews):
            if conclusion in reviews:
                correct_counts[candidate_rule] += 1
            else:
                incorrect_counts[candidate_rule] += 1


# ### Compute the confidence for each rule by dividing the correct count by the total number of times the rule

# In[20]:


rule_confidence = {candidate_rule: correct_counts[candidate_rule] / float(correct_counts[candidate_rule] + incorrect_counts[candidate_rule]) for candidate_rule in candidate_rules}


# In[24]:

links = pd.read_csv(os.path.join(file_path,'./dataset/links.csv'))

def get_movie_imdbId_from_id(movie_id):
    try:
        imdb_object = links[links['movieId'] == movie_id]['imdbId']
        imdbId = imdb_object.values[0]
        return imdbId
    except:
        return -1

def get_movieId_from_imdbId(imdbId):
    try:
        movie_object = links[links['imdbId'] == imdbId]['movieId']
        movieId = movie_object.values[0]
        return movieId
    except:
        return -1

#print(get_movie_imdbId_from_id(260))
#print(get_movieId_from_imdbId(76759))


# ### Print top five rules by sorting this confidence dictionary

# In[25]:
    
import sys
from operator import itemgetter

# Get imdbIds argument from command line and convert it to movieId

# If number of arguments greater than 1, then give recomended movieId 
# then convert it to imdbId
if(len(sys.argv) > 1):
    
    movieId_list = []
    for element in sys.argv[1:]:
        movieId_list.append(get_movieId_from_imdbId(int(element)))
    
    sorted_confidence = sorted(rule_confidence.items(), key=itemgetter(1), reverse=True)
    con_list = [] # eliminate same conclusion movie id to return just unique movie id
    for index in range(len(sorted_confidence)):
        (premise, conclusion) = sorted_confidence[index][0]
		# favorites list : 260, 47 movie id looks these id from promise list 
		# if the fav list is superset of premise and conculusion(recommended) is not in conclusion list (provides returning unique movie id)
		# than return movie id and convert it to imdbId to use in movie recommendation app
        if(set(movieId_list).issuperset(premise) and conclusion not in con_list): 
            print(get_movie_imdbId_from_id(conclusion))
            con_list.append(conclusion)


