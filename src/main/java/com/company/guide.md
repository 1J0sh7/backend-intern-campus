# Phase 2 - Java Fundamentals Walkthrough

## Project Overview

This project demonstrates core Java concepts including encapsulation, inheritance, enums, packages, compilation, and execution.





## Compilation Process

### What Happened When You Ran javac

The javac command read your .java source files and converted them into bytecode (.class files).

The bytecode is platform-independent and can run on any system with a JVM.

Command used:
javac -d . -sourcepath "Phase 2 - Java Fundamentals Walkthrough/src/main/java" "Phase 2 - Java Fundamentals Walkthrough/src/main/java/com/company/Main.java"





## The Error You Fixed

### Error Message

error: constructor Customer in class Customer cannot be applied to given types
required: String,String,String
found: String,String,String,String

### What It Meant

Address.java tried to call super(name, email, phone, address) but Customer.java constructor only accepts 3 parameters.

### The Fix

Before (wrong):
super(name, email, phone, address);

After (correct):
super(name, email, phone);

### Concepts Learned

Constructor - Special method that runs when creating an object.

super() - Calls the parent class constructor.

Parameters - Values passed to a method or constructor.

Method signature - Method name plus parameter types must match exactly.





## Inheritance

### How You Used It

public class Address extends Customer {

Address INHERITS name, email, phone from Customer.

Address ADDS street, city.

}

### Key Terms

extends - Keyword to inherit from another class.

Parent class - Customer - the class being inherited from.

Child class - Address - the class that inherits.

Inheritance - Child gets ALL fields and methods from Parent.

super() - Calls Parent constructor from Child.

### Why You Used It

Address IS A Customer (has name, email, phone).

Address also HAS street, city.

Inheritance allowed reuse without rewriting Customer fields.





## What Happened When You Ran the Program

### Command

java com.company.Main

### What Happened

JVM loaded Main.class.

Found the main method.

Executed code line by line.

Printed output to terminal.

### Key Terms

JVM - Java Virtual Machine - runs bytecode.

main method - Entry point of any Java application.

public static void main(String[] args) - Required signature to run.





## OOP Concepts You Demonstrated

### Encapsulation

Used private fields with public getters and setters in all classes.

### Inheritance

Address extends Customer.

### Enum

LoanStatus - fixed set of 5 constants.

### Constructor

Used in every class to create objects with the new keyword.

### this

Refers to current object's fields.

### super

Refers to parent class constructor.





## Package Structure Used

src/main/java/com/company/
├── Main.java
├── customer/
│   ├── Customer.java
│   └── Address.java
├── loan/
│   ├── LoanStatus.java
│   ├── LoanProduct.java
│   └── LoanApplication.java
└── payment/
└── Repayment.java

### Why Packages Matter

Organizes code by domain.

Avoids naming conflicts.

Controls access between classes.

Industry standard practice.





## Commands Used

javac -d . -sourcepath "Phase 2 - Java Fundamentals Walkthrough/src/main/java" "Phase 2 - Java Fundamentals Walkthrough/src/main/java/com/company/Main.java"

Compiles all Java files and creates .class files.

java com.company.Main

Runs the compiled program.





## Next Steps

Add JUnit tests to verify your code works.

Write notes explaining OOP concepts.

Refactor code to improve structure.





## Progress Checklist

JF-001 - JDK, JVM and execution model

1. Create project - Done
2. Write code - Done
3. Compile manually - Done
4. Add tests - Next
5. Explain concept - Next
6. Refactor - Next

JF-003 - Object-oriented programming

1. Create project - Done
2. Write code - Done
3. Compile manually - Done
4. Add tests - Next
5. Explain concept - Next
6. Refactor - Next