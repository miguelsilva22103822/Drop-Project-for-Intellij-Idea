# Drop Project Plugin for Intellij Idea with GPT support

This is an extension of the Drop Project Student plugin for Intellij IDEA which adds GPT-related functionality. It aims at improving student support when dealing with auto-graded assignments, and it was developed with input from both CS teachers and students and aims.

More concretely, the following functionality was added to the original (v9.3.0) plugin:
* The build/submission report now displays an "Ask GPT" button for each error reported by the Drop Project AAT;
* Chat-based interface, similar to ChatGPT / Gemin;
* Capacity to simultaneously ask for 2 solutions and compare them using a Git diff-like interface;
* Interaction logs are created locally (not sent to any server);
* Students can vote on wether GPT's reply was useful or not (this information is logged locally and can be explored for research purposes);

This will soon be integrated in the official GitHub repository.

![[Static Badge](https://img.shields.io/badge/version-v0.9.3-blue)](https://img.shields.io/badge/version-v0.9.3-blue)
[![JetBrains plugins](https://img.shields.io/jetbrains/plugin/d/21870-drop-project)](https://img.shields.io/jetbrains/plugin/d/21870-drop-project)

## The new and Improved Drop Project Plugin!

Drop Project is an open-source automated assessment tool that checks student programming projects for correctness and
quality

This plugin was made in order to help students simplify the process of submitting and review code to Drop Project
website, all-in-one!

    Add assignments
    Check assignments details
    Submit assignments
    Review assignments results

## Requirements

* IntelliJ IDEA installed
* Access to Drop Project Website

## How to get it

* Access the IDE settings in File > Settings, or access directly through the settings icon in the upper right corner
* Go to the Plugins section
* Go to the Marketplace section in plugins and search for Drop Project
* Select Install

## How to use

* This plugin is mainly concentrated in a toolwindow, which is probably in the right panel of your IDE
* Login with your credentials (Your name; Your Drop Project Token)
* First icon in the toolbar is to add an assignment
* Second icon is to submit your code
* Third icon is to refresh the assignment list
* The last icon is to log out
* If you submit an assignment, a forth icon will appear which is used to check the buid report of your last submission
* You can access some aditional plugin settings in the settings icon on the toolwindow top title
