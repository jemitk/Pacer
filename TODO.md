TODO
====

In this file, we'll have some various todo items for the project. Those wil
be broken up into 3 groups: known issues/bugs, concrete improvement ideas,
and feature requests.

## Known Issues/ Bugs

* Write unit tests, lol

## Improvement ideas

## Feature requests

All features are preceded by their priority: P0 for trivial/ important
additions, to P4 for incredibly difficult/ useless additions

* P0: Workouts: increase and decrease song paces based on a predefined sequence,
or workout
* P0: Be able to view all songs currently recognized by the phone, along with
their current bpm on file
* P1: Occasionally survey user after choosing the song to run to: good or bad, if bad, too slow or too fast, then log the inaccuracy and adjust the choosen song
* P1: Be able to re-tap the beat to individual songs 
* P1: The ability to store past workouts
* P1: Keeping track of step counts to add to stored workouts
* P2: Adding GPS integration to calculate distance traveled/ route
* P2: iOS port
* P3: Windows phone port


## Implemented TODOs

* App crashes when songs present in it's library aren't on the phone. ([track here](https://github.com/jemitk/Pacer/issues/4))
* Reject outlying taps in the tap-input to get better measurements ([track here](https://github.com/jemitk/Pacer/issues/5))
* Change the screen's during tap-input to let the user know when the input
song has changed ([commit](https://github.com/jemitk/Pacer/commit/834e96ff319cd0847c579e5fc0ba069b1aa9e674)
that finished it)
* Display the current input song during tap-input
([commit](https://github.com/jemitk/Pacer/commit/e2444f886eb362f33d8caa5153de60fe48540c3f)
 that finished it)
* Make the settings button actually go to a settings page, put remove library
on that screen ([commit](https://github.com/jemitk/Pacer/commit/c8a89ca4e3b287fbf603061559c11fbc24d3d21e) that finished it)
