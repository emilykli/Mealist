Capstone App
===

# Mealist

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Allows users to select which recipes they want to cook for the week and generates a shopping list of items they need to make everything. Also generates nutritional information based on what is being made.

### App Evaluation
<!-- [Evaluation of your app across the following attributes] -->
- **Category:** Lifestyle
- **Mobile:** Mobile is most effective for this app because users can carry their phone around while shopping.
- **Story:** Lets users plan their grocery shopping based on the things they want to eat.
- **Market:** People who are new to cooking and shopping for themselves, people who want to be more organized.
- **Habit:** Planning meals every week
- **Scope:** V1 would allow the user to log in, save the recipes selected by the user, and show the recipe for the day the recipe was selected for. V2 would generate a list of ingredients for the week based on the selected recipes. V3 would generate nutritional information for the week based on the selected recipes.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Users can log in and save recipes
* Saved recipes show nutrition information
* Saved recipes used to generate grocery shopping list for the week
* Users can associate their saved recipes with a date/time
* Checkboxes to mark what you've bought already
* ...

**Optional Nice-to-have Stories**

* Users can input what they already have
* Users can see what their friends are making
* If you buy too much, the app remembers that you already have that ingredient
* Calendar API
* [fill in your required user stories here]
* ...

### 2. Screen Archetypes

* Login/Register screens
    * users can log in and save recipes
* Home Screen/Day at a Glance
    * Users can associate their saved recipes with a date/time
    * Saved recipes show nutrition information
* Generated Grocery List
    * Saved recipes used to generate grocery shopping list for the week
    * Checkboxes to mark what's been bought already
* Meal plan creation page
    * Save recipes
* Recipe search page
    * Save recipes
    * ...

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Generated List
* Day at a Glance
* Create Meal Plan

**Flow Navigation** (Screen to Screen)

* Recipe Search Page
    * when + is clicked in Create Meal Plan

<!-- * [list second screen here]
   * [list screen navigation here]
   * ... -->

## Wireframes
<img src="./wireframe_sketch.jpg" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema
### Models
#### User
| Property      | Type     | Description |
   | ------------- | -------- | ------------|
| objectId      | String   | unique id for the user|
| savedRecipes  | Array    | list of recipes saved by user |
| username       | String   | user's username|
| name | String   | user's name |

#### Grocery List
| Property      | Type     | Description |
   | ------------- | -------- | ------------|
| objectId      | String   | unique id for the grocery list |
| owner        | Pointer to User| grocery list owner |
| ingredients   | Array | list of ingredient strings |
| quantities     | Array | list of ingredient quantities (index of ingredient corresponds to index of quantity)|
| units     | Array | list of quantity units (index of unit corresponds to index of quantity)|
| startRange     | DateTime |first day grocery list is relevant for|
| endRange     | DateTime |last day grocery list is relevant for|

#### Meal Plan
| Property      | Type     | Description |
   | ------------- | -------- | ------------|
| objectId      | String   | unique id for the meal plan |
| owner        | Pointer to User| meal plan owner |
| breakfast   | Array | list of recipes |
| lunch     | Array | list of recipes|
| dinner     | Array | list of recipes|
| dayOf     | DateTime | date that meal plan is for |
#### Recipe
| Property      | Type     | Description |
   | ------------- | -------- | ------------|
| objectId      | String   | unique id for the recipe|
| meal time | String | breakfast lunch or dinner |
| ingredients     | Array | list of ingredients for recipe |
| quantities | Array | list of quantities for recipe|
| units | Array | list of units for quantities |



### Networking
- Login Screen
    - (Read/GET) Log in based on provided user info
    ```java
          ParseUser.logInInBackground(username, password, new LogInCallback() {
           @Override
           public void done(ParseUser user, ParseException e) {
               if (e != null) {
                   Log.e(TAG, "Issue with login", e);
                   return;
               }

               else {
                   goMainActivity();
               }
           }
       });
        ```
- Register Screen
    - (Create/POST) Create a new user
- Make Meal Plan
    - (Read/GET) Query breakfast, lunch, dinner on dayOf where the owner is the User

    - (Create/POST) Add recipe for meal
    - (Delete) Delete recipe from meal
- Recipe Adder
    - (Read/GET) Query saved recipes for user
    - (Update/PUT) Update saved recipes for user
- Grocery List
    - (Update/PUT) Update checkboxes for which groceries have been bought


### API endpoints

#### Spoonacular
- Base URL - https://api.spoonacular.com/recipes
  HTTP Verb | Endpoint | Description
  ----------|----------|------------
  `GET`    | /complexSearch | search recipes

<!-- Example Request and Response
GET https://api.spoonacular.com/recipes/complexSearch?query=pasta&maxFat=25&number=2
{
    "offset": 0,
    "number": 2,
    "results": [
        {
            "id": 716429,
            "title": "Pasta with Garlic, Scallions, Cauliflower & Breadcrumbs",
            "calories": 584,
            "carbs": "84g",
            "fat": "20g",
            "image": "https://spoonacular.com/recipeImages/716429-312x231.jpg",
            "imageType": "jpg",
            "protein": "19g"
        },
        {
            "id": 715538,
            "title": "What to make for dinner tonight?? Bruschetta Style Pork & Pasta",
            "calories": 521,
            "carbs": "69g",
            "fat": "10g",
            "image": "https://spoonacular.com/recipeImages/715538-312x231.jpg",
            "imageType": "jpg",
            "protein": "35g"
        }
    ],
    "totalResults": 86
} -->