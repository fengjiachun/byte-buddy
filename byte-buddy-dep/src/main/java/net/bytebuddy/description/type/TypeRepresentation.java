package net.bytebuddy.description.type;

import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.generic.GenericTypeDescription;
import net.bytebuddy.description.type.generic.GenericTypeList;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface TypeRepresentation extends Iterable<TypeRepresentation> {

    /**
     * Returns the name of the type. For generic types, this name is their {@link Object#toString()} representations. For a non-generic
     * type, it is the fully qualified binary name of the type.
     *
     * @return The name of this type.
     */
    String getTypeName();

    /**
     * <p>
     * Returns the generic super type of this type.
     * </p>
     * <p>
     * Only non-generic types ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#NON_GENERIC}) and parameterized types
     * ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#PARAMETERIZED}) define a super type. For a generic array type,
     * ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#GENERIC_ARRAY}), a description of {@link Object} is returned.
     * For other generic types, an {@link IllegalStateException} is thrown.
     * </p>
     *
     * @return The generic super type of this type or {@code null} if no such type exists.
     */
    GenericTypeDescription getSuperType();

    /**
     * <p>
     * Returns the generic interface types of this type.
     * </p>
     * <p>
     * Only non-generic types ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#NON_GENERIC}) and parameterized types
     * ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#PARAMETERIZED}) define a super type. For a generic array type,
     * ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#GENERIC_ARRAY}), a list of {@link java.io.Serializable} and
     * {@link Cloneable}) is returned. For other generic types, an {@link IllegalStateException} is thrown.
     * </p>
     *
     * @return The generic interface types of this type.
     */
    GenericTypeList getInterfaces();

    /**
     * <p>
     * Returns a list of field descriptions that are declared by this type. For parameterized types, all type variables of these fields are
     * resolved to the values of the type variables.
     * </p>
     * <p>
     * Only non-generic types ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#NON_GENERIC}) and parameterized types
     * ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#PARAMETERIZED}) define a super type. For a generic array type,
     * ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#GENERIC_ARRAY}), an empty list is returned. For other generic
     * types, an {@link IllegalStateException} is thrown.
     * </p>
     *
     * @return A list of fields that are declared by this type.
     */
    FieldList<?> getDeclaredFields();

    /**
     * <p>
     * Returns a list of method descriptions that are declared by this type. For parameterized types, all type variables used by these methods
     * are resolved to the values of the type variables.
     * </p>
     * <p>
     * Only non-generic types ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#NON_GENERIC}) and parameterized types
     * ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#PARAMETERIZED}) define a super type. For a generic array type,
     * ({@link net.bytebuddy.description.type.generic.GenericTypeDescription.Sort#GENERIC_ARRAY}), an empty list is returned. For other
     * generic types, an {@link IllegalStateException} is thrown.
     * </p>
     *
     * @return A list of methods that are declared by this type.
     */
    MethodList<?> getDeclaredMethods();

    GenericTypeDescription asGenericType();

    /**
     * An iterator that iterates over a type's class hierarchy.
     */
    class TypeHierarchyIterator implements Iterator<TypeRepresentation> {

        /**
         * The next type to represent.
         */
        private TypeRepresentation nextType;

        /**
         * Creates a new iterator.
         *
         * @param initialType The initial type of this iterator.
         */
        public TypeHierarchyIterator(TypeRepresentation initialType) {
            nextType = initialType;
        }

        @Override
        public boolean hasNext() {
            return nextType != null;
        }

        @Override
        public TypeRepresentation next() {
            if (!hasNext()) {
                throw new NoSuchElementException("End of type hierarchy");
            }
            try {
                return nextType;
            } finally {
                nextType = nextType.getSuperType();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("remove");
        }

        @Override
        public String toString() {
            return "TypeRepresentation.TypeHierarchyIterator{" +
                    "nextType=" + nextType +
                    '}';
        }
    }
}
