/*
 * Copyright (c)  RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package org.nest.symboltable.symbols;

import com.google.common.collect.Lists;
import de.monticore.symboltable.CommonSymbol;

import java.util.Collection;
import java.util.Optional;

/**
 * Represents an ordinary type symbol.
 *
 */
public class TypeSymbol extends CommonSymbol {

  public final static TypeSymbolKind KIND = new TypeSymbolKind();

  private final static Collection<MethodSymbol> builtInMethods = Lists.newArrayList();

  private final Type type;

  public TypeSymbol(final String name, final Type type) {
    super(name, KIND);
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public void addBuiltInMethod(final MethodSymbol builtInMethod) {
    builtInMethods.add(builtInMethod);
  }

  public Optional<MethodSymbol> getBuiltInMethod(final String methodName) {
    // TODO signature must be considered
    return builtInMethods.stream().filter(method -> method.getName().equals(methodName)).findFirst();
  }

  @Override
  public String toString() {
    return "TypeSymbol(" + getFullName() + "," + type + ")";
  }

  public enum Type { UNIT, PRIMITIVE}
}