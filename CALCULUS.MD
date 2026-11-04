# Comprehensive Documentation: `AlgebraicTree` & Mathematical Expression Engine

---

## 1. Overview & Usage

`AlgebraicTree` is the core mathematical expression parser, evaluation engine, and symbolic/numeric calculus processor in the `empty3-library` ecosystem. It parses mathematical formulas given as strings, builds an expression tree (`TreeNode`), supports parameter binding, evaluates scalar and vector values, and performs symbolic differentiation, antidifferentiation, and numerical integration.

### Basic Initialization & Evaluation
```java
import one.empty3.library1.tree.AlgebraicTree;

// Create and construct an algebraic tree
AlgebraicTree tree = new AlgebraicTree("3.0 * (x ^ 2.0) + 5.0 * x + 2.0");
tree.construct();

// Set variable parameter values
tree.setParameter("x", 2.0);

// Evaluate result
double result = tree.eval().getElem();
```

---

## 2. Syntax & Expression Grammar

Expressions follow standard mathematical notation supporting operator precedence, parentheses, variables, constants, and function calls.

- **Operators**:
  - Addition / Subtraction: `+`, `-`
  - Multiplication / Division: `*`, `/`
  - Exponentiation / Power: `^` (e.g., `x ^ 3.0`)
- **Grouping**: Parentheses `(...)` for explicit precedence control.
- **Variables**: Alphanumeric identifiers (e.g., `x`, `y`, `t`, `theta`).
- **Constants**: Real numeric literals (e.g., `42.0`, `3.14159`).

---

## 3. Built-in Mathematical Functions

`AlgebraicTree` and its associated node types support standard mathematical functions:
- **Trigonometric**: `sin(x)`, `cos(x)`, `tan(x)`
- **Logarithmic & Exponential**: `log(x)` (natural logarithm), `exp(x)`
- **Power & Roots**: `sqrt(x)` (or `x ^ 0.5`), `abs(x)`

---

## 4. Vector Aggregations & Multi-Component Expressions

### Vector Components
Multi-component expressions (vectors, parametric curves, surfaces) are defined using comma-separated expressions:
```java
AlgebraicTree vecTree = new AlgebraicTree("t, t ^ 2.0, sin(t)");
vecTree.construct();
```

### Vector Aggregation Functions
Built-in aggregate functions available across vector or argument lists (`one.empty3.library1.tree.Functions`):
- `sum(...)`: Calculates the sum of arguments.
- `product(...)`: Calculates the product of arguments.
- `avg(...)`: Computes the arithmetic mean.
- `median(...)`: Computes statistical variance/dispersion metrics.
- `count(...)`: Returns the number of arguments.

---

## 5. Calculus Capabilities

`AlgebraicTree` provides a powerful suite of symbolic and numerical calculus operations:

### A. Symbolic Differentiation
Compute the exact derivative with respect to a given variable:
```java
AlgebraicTree tree = new AlgebraicTree("x * sin(x)");
tree.construct();

// f'(x) = sin(x) + x * cos(x)
AlgebraicTree diff = tree.derivative("x");
diff.setParameter("x", 1.0);
double derivativeValue = diff.eval().getElem();
```
*Supported derivative rules include constant rule, power rule, product rule, quotient rule, chain rule, and trigonometric function derivatives.*

### B. Antiderivatives (Symbolic Integration)
Compute indefinite integrals (antiderivatives):
```java
AlgebraicTree anti = tree.antiderivative("x");
anti.setParameter("x", 3.0);
double antiValue = anti.eval().getElem();
```

### C. Definite Numerical Integration (Quadrature)
Compute definite integrals over an interval $[a, b]$:
```java
// \int_0^2 x^2 dx
AlgebraicTree tree = new AlgebraicTree("x ^ 2.0");
tree.construct();
double definiteIntegral = tree.integrate("x", 0.0, 2.0); // Returns ~2.6667
```

### D. Vector Definite Integration
Integrate vector-valued expressions component-wise:
```java
// \int_0^1 [t, t^2] dt
AlgebraicTree tree = new AlgebraicTree("t, t ^ 2.0");
tree.construct();
StructureMatrix<Double> vectorIntegral = tree.integrateVector("t", 0.0, 1.0);
```

### E. Multi-Dimensional Iterated Integrals (2D)
Compute double integrals over rectangular domains:
```java
// \int_0^1 \int_0^1 (x + y) dx dy
AlgebraicTree tree = new AlgebraicTree("x + y");
tree.construct();
double doubleIntegral = tree.integrate2D("x", 0.0, 1.0, "y", 0.0, 1.0); // Returns 1.0
```

---

## 7. The `calcmath` Language: Inline Calculus Syntax

The parser language powering `AlgebraicTree` is known as **`calcmath`**. It supports direct, inline evaluation of calculus operations (`integral` and `derivative`) directly inside formula strings.

### A. Inline Definite Integral (`integral`)
To compute definite integrals directly within an expression string, use the `integral` function with 4 arguments:
```
integral(variableName, expression, lowerBound, upperBound)
```

**Example in Java / `calcmath`**:
```java
AlgebraicTree tree = new AlgebraicTree("integral(x, x ^ 2.0, 0.0, 2.0)");
tree.construct();
double result = tree.eval().getElem(); // Evaluates to ~2.6667 (\int_0^2 x^2 dx)
```

### B. Inline Derivative (`derivative`)
To compute and evaluate a derivative directly within an expression string, use the `derivative` function with 3 arguments:
```
derivative(variableName, expression, evaluationPoint)
```

**Example in Java / `calcmath`**:
```java
AlgebraicTree tree = new AlgebraicTree("derivative(x, x ^ 2.0, 2.0)");
tree.construct();
double result = tree.eval().getElem(); // Evaluates to 4.0 (d/dx(x^2) evaluated at x = 2.0)
```