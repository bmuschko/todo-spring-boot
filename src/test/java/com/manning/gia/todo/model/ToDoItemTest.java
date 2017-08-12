package com.manning.gia.todo.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class ToDoItemTest {
    private static final Long ITEM_ID = 123L;
    private static final String ITEM_NAME = "Buy milk";

    @Test
    public void testStringRepresentation() {
        ToDoItem toDoItem = createToDoItem(ITEM_ID, ITEM_NAME, true);
        assertEquals(toDoItem.toString(), "123: Buy milk [completed: true]");
    }

    @Test
    public void testCanCompare() {
        ToDoItem toDoItem1 = createToDoItem(ITEM_ID, ITEM_NAME, true);
        ToDoItem toDoItem2 = createToDoItem(ITEM_ID, ITEM_NAME, true);
        assertTrue(toDoItem1.equals(toDoItem2));

        toDoItem2.setName("Wash dishes");
        assertFalse(toDoItem1.equals(toDoItem2));
    }

    private ToDoItem createToDoItem(Long id, String name, boolean completed) {
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setId(id);
        toDoItem.setName(name);
        toDoItem.setCompleted(completed);
        return toDoItem;
    }
}
