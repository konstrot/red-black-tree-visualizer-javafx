# Red-Black Tree Visualization with JavaFX

A JavaFX application that visualizes insertion into a red-black tree step by step.

Instead of showing only the final tree, the application displays intermediate states, recoloring operations, rotations, and temporary rule violations during balancing.

## Features

* step-by-step visualization of node insertion;
* intermediate states during tree balancing;
* node recoloring and rotations;
* display of black `NIL` leaves;
* black-node count for every root-to-`NIL` path;
* validation of the main red-black tree properties;
* highlighting of nodes involved in the current step;
* manual and automatic playback.

The application interface is currently in German.

## Controls

| Button          | Function                 |
| --------------- | ------------------------ |
| **Zurück**      | previous state           |
| **Weiter**      | next state               |
| **Automatisch** | start automatic playback |
| **Pause**       | pause automatic playback |
| **Neu starten** | return to the empty tree |

A gold outline marks the nodes involved in the current operation.

## Default Sequence

The current version uses the following insertion sequence:

```text
3, 5, 8, 10, 12, 15, 17, 20, 22, 25, 27, 30, 32, 35, 37
```

It demonstrates red-red violations, recoloring, left rotations, and intermediate balancing states.

## Validation

For every stored state, the application checks:

1. the root is black;
2. no red node has a red parent or red child;
3. every root-to-`NIL` path contains the same number of black nodes.

Intermediate states may temporarily violate these rules and are marked with:

```text
Zwischenzustand während der Reparatur
```

`BH` shows the number of black nodes on a complete path from the root to a `NIL` leaf. The black root and the black `NIL` leaf are included in this count.

## Download

A ready-to-use Windows version is available on the GitHub **Releases** page.

The Windows package includes the required Java runtime and can be started without installing a development environment.

## Project Structure

```text
rbt2/
├── src/
│   ├── Main.java
│   ├── RBTree.java
│   ├── RBTreeNode.java
│   ├── RBTreeVisualizer.java
│   └── TreeStep.java
├── README.md
├── LICENSE
└── .gitignore
```

| File                    | Responsibility                                                 |
| ----------------------- | -------------------------------------------------------------- |
| `Main.java`             | JavaFX interface, navigation, and automatic playback           |
| `RBTree.java`           | insertion, recoloring, rotations, and step generation          |
| `RBTreeNode.java`       | red-black tree node                                            |
| `TreeStep.java`         | stored tree state and rule validation                          |
| `RBTreeVisualizer.java` | rendering of nodes, edges, `NIL` leaves, and black-node counts |

## Running from Source

### Requirements

* JDK 17;
* JavaFX SDK 17.

### Compile

Open PowerShell in the project directory:

```powershell
$JAVA_HOME = "C:\path\to\jdk-17"
$JAVAFX_HOME = "C:\path\to\javafx-sdk-17"

New-Item -ItemType Directory -Force -Path ".\bin" | Out-Null
$sources = Get-ChildItem ".\src\*.java" | ForEach-Object { $_.FullName }

& "$JAVA_HOME\bin\javac.exe" `
  --module-path "$JAVAFX_HOME\lib" `
  --add-modules javafx.controls,javafx.graphics `
  -encoding UTF-8 `
  -d ".\bin" `
  $sources
```

### Run

```powershell
& "$JAVA_HOME\bin\java.exe" `
  --module-path "$JAVAFX_HOME\lib" `
  --add-modules javafx.controls,javafx.graphics `
  -cp ".\bin" `
  Main
```

## Current Limitations

* only insertion is implemented;
* the default sequence demonstrates only left rotations;
* rotations are displayed as consecutive states rather than animations;
* duplicate keys are not handled as a separate case;
* automated tests are not yet included.

## Background

The application was developed as a technical prototype for a Design Thinking project focused on visualizing intermediate states during red-black tree balancing.

An earlier version generated static diagrams using Python and Graphviz:

* https://github.com/konstrot/red-black-tree-visualization

## License

This project is licensed under the terms described in the [LICENSE](LICENSE) file.

## Author

Konstantin Rotaermel
GitHub: [@konstrot](https://github.com/konstrot)
