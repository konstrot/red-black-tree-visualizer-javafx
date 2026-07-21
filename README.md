# Red-Black Tree — Step-by-Step Visualization with JavaFX

A didactic JavaFX application that presents insertion into a red-black tree not only as a final result, but as a sequence of individual repair steps.

The visualization shows when a new node is inserted, when a red-red violation occurs, which nodes are recolored, and how rotations restore the properties of the red-black tree.

> This project was created as a technical prototype in a Design Thinking project. The learning problem is that many students find it difficult to understand the intermediate states of red-black tree balancing and often do not know which insertion sequence will trigger a particular repair case.

## Features

- step-by-step visualization of every insertion;
- visible intermediate states during repair;
- visualization of node recoloring;
- visualization of left and right rotations when they occur in the selected insertion sequence;
- display of black `NIL` leaves;
- display of the black-node count for every root-to-`NIL` path;
- continuous validation of the main red-black tree properties;
- highlighting of the nodes involved in the current step;
- manual navigation with **Zurück** and **Weiter**;
- automatic playback with **Automatisch** and **Pause**;
- return to the initial state with **Neu starten**.

The application interface is currently in German.

## Displayed Rules

For every stored state, the application checks the following properties:

1. **The root is black.**
2. **A red node does not have a red parent or red child.**
3. **Every path from the root to a `NIL` leaf contains the same number of black nodes.**

Intermediate states during a repair operation may temporarily violate one or more of these rules. The application identifies such a state with the message:

```text
Zwischenzustand während der Reparatur
```

This means:

```text
Intermediate state during repair
```

After the repair is complete, the tree is displayed as a valid red-black tree again.

## Meaning of Black Height

In this visualization, `BH` represents the number of black nodes on a complete path from the root to a `NIL` leaf. Both a black root and the black `NIL` leaf are included in the count.

For example:

```text
BH=4
```

means that the corresponding root-to-`NIL` path contains four black nodes.

A red-black tree does not require all paths to have the same geometric length. Only the number of black nodes on the paths must be equal.

## Default Scenario

The application currently uses the strictly increasing insertion sequence:

```text
3, 5, 8, 10, 12, 15, 17, 20, 22, 25, 27, 30, 32, 35, 37
```

This sequence reliably produces:

- red-red violations;
- recoloring operations;
- left rotations;
- temporary changes in black height during repair.

Because the sequence is strictly increasing, it does not produce right rotations. Additional guaranteed scenarios for demonstrating all major repair cases are:

```java
int[] LEFT_ROTATION  = {10, 20, 30};
int[] RIGHT_ROTATION = {30, 20, 10};
int[] LEFT_RIGHT     = {30, 10, 20};
int[] RIGHT_LEFT     = {10, 30, 20};
int[] RECOLORING     = {10, 5, 15, 1};
```

## Controls

| Button | Function |
|---|---|
| **Zurück** | shows the previous stored state |
| **Weiter** | shows the next stored state |
| **Automatisch** | starts automatic playback |
| **Pause** | pauses automatic playback |
| **Neu starten** | returns to the empty tree |

A gold outline marks the nodes involved in the current repair step.

## Project Structure

```text
rbt2/
├── src/
│   ├── Main.java
│   ├── RBTree.java
│   ├── RBTreeNode.java
│   ├── RBTreeVisualizer.java
│   └── TreeStep.java
├── bin/                 # generated during compilation
├── README.md
└── .gitignore
```

### Class Responsibilities

| File | Responsibility |
|---|---|
| `Main.java` | JavaFX user interface, navigation, and automatic playback |
| `RBTree.java` | insertion algorithm, recoloring, rotations, and step events |
| `RBTreeNode.java` | data structure representing a tree node |
| `TreeStep.java` | immutable snapshot of a tree state and validation of displayed rules |
| `RBTreeVisualizer.java` | graphical rendering of nodes, edges, `NIL` leaves, and black heights |

## Requirements

- Windows, Linux, or macOS;
- JDK 17;
- JavaFX SDK 17;
- optionally, Visual Studio Code with the **Extension Pack for Java**.

The project does not use Maven or Gradle. JavaFX is included as a local SDK.

## Compile and Run Without Maven

### 1. Install the JavaFX SDK

Download JavaFX SDK 17 and extract it, for example to:

```text
C:\java\javafx-sdk-17.0.18
```

The `lib` directory should contain files such as:

```text
javafx.base.jar
javafx.controls.jar
javafx.graphics.jar
```

### 2. Compile the Project

Open PowerShell in the project directory and run:

```powershell
$JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot"
$JAVAFX_HOME = "C:\java\javafx-sdk-17.0.18"

New-Item -ItemType Directory -Force -Path ".\bin" | Out-Null
$sources = Get-ChildItem ".\src\*.java" | ForEach-Object { $_.FullName }

& "$JAVA_HOME\bin\javac.exe" `
  --module-path "$JAVAFX_HOME\lib" `
  --add-modules javafx.controls,javafx.graphics `
  -encoding UTF-8 `
  -d ".\bin" `
  $sources
```

Adjust the paths to match your local JDK and JavaFX installations.

### 3. Run the Application

```powershell
& "$JAVA_HOME\bin\java.exe" `
  --module-path "$JAVAFX_HOME\lib" `
  --add-modules javafx.controls,javafx.graphics `
  -cp ".\bin" `
  Main
```

## Visual Studio Code

For a project without a build tool, JavaFX can be referenced in `.vscode/settings.json`:

```json
{
    "java.project.sourcePaths": [
        "src"
    ],
    "java.project.outputPath": "bin",
    "java.project.referencedLibraries": [
        "C:/java/javafx-sdk-17.0.18/lib/*.jar"
    ]
}
```

These paths are local and must be adjusted on every computer. Personal `.vscode` files containing absolute paths should therefore not be committed unchanged to a public GitHub repository.

## Current Limitations

- Only insertion is implemented; deletion and search are not part of the user interface.
- Because the default sequence is increasing, the standard scenario demonstrates only left rotations.
- Rotations are shown as consecutive snapshots rather than smooth node movement.
- The demonstration is intended for unique keys; duplicate keys are not treated as a separate learning case.
- Automated tests are not yet included.

## Planned Improvements

- selectable guaranteed scenarios for recoloring, left rotation, right rotation, and double rotations;
- visual identification of the **new node**, **parent**, **grandparent**, and **uncle**;
- arrows and animations showing rotation direction;
- grouping of steps by insertion operation and repair phase;
- automated tests for red-black tree invariants;
- customizable insertion sequences;
- responsive visualization when the window is resized.

## Design Thinking Context

The prototype addresses a specific learning problem: balancing a red-black tree is difficult to understand when it is explained only through text or static diagrams. The application makes internal state changes visible and reduces the need for learners to guess suitable input values themselves.

An earlier project iteration generated static visualizations using Python and Graphviz. The JavaFX version extends that approach with interactive navigation, intermediate states, and visible validation of black height.

Related Python project:

- https://github.com/konstrot/red-black-tree-visualization

## Author

Konstantin Rotaermel  
GitHub: [@konstrot](https://github.com/konstrot)
