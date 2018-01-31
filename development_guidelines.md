# Guidelines for Software Development

*Target audience*: Software developers who are contributing to the Gokart project, i.e. a safety critical application

## Modality

* Make your work visible: `push` your code modifications to `github` no later than at the end of the day. That way, reviewers can give feedback immediately and coordinate the work better.
* Only commit your own source code.
* If you adapt code snippets from another source, state the origin and give credit, for instance via a URL.
* Do not commit a library that was created by a 3rd party. Instead, such a repository can be forked and maintained separately.
* Do not commit binary files, for instance: log files, image files, `PDF`s, ... The exception are small files (less than `1[MB]`) that are for testing purposes unlikely to change in the future.

## Scope

The staff supports you to make your project a success.
Your Thesis-/Semester project builds on the work of others.
We encourage and expect you to make some contributions outside the scope of your project.
In the `github` issues, there is a list of desirable features that the whole group would benefit from.
Besides these ideas there are actions that advance the project without the risk of breaking existing functionality: inserting comments and writing tests.

In conclusion: please spend some time on code items that may not be the highlights of your report, but are appreciated by the group.

## Design

* many small source files
* functions with few lines of code
* simplicity
* modularity
* tests and test coverage
* code format

## Language

The software framework of the Gokart allows the use of different languages.
The processes exchange packets of information via the message passing standard `LCM`.

### Java 8

So far, the Gokart code is written in `Java 8`

* sensor interfaces
* actuator interfaces
* emergency modules
* controllers for steering and velocity
* motion planning
* localization by lidar
* visualization in 2D and 3D

### Python

The use of Python *may* be advantageous for modules that involve

* camera calibration
* image processing
* machine learning

### C++

There are at least 6 ways to initialize a pointer to be *null* in `c++`

    myclass() : ptr {} { ...
    myclass() : ptr {0} { ...
    myclass() : ptr {nullptr} { ...
    myclass() : ptr ({}) { ...
    myclass() : ptr ({0}) { ...
    myclass() : ptr (nullptr) { ...

