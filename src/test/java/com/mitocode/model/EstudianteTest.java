package com.mitocode.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstudianteTest {

    @Test
    void testEqualsAndHashCodeByIdOnly() {
        Estudiante est1 = new Estudiante(1, "Jaime", "Medina", "12345678", 32);
        Estudiante est2 = new Estudiante(1, "Otro", "Nombre", "87654321", 20);
        Estudiante est3 = new Estudiante(2, "Jaime", "Medina", "12345678", 32);

        assertEquals(est1, est2);
        assertEquals(est1.hashCode(), est2.hashCode());
        assertNotEquals(est1, est3);
    }
}
