package com.manning.gia.todo.repository;

import com.manning.gia.todo.model.ToDoItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ToDoRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ToDoRepository repository;

    @Test
    public void testCanFindSingleNonExistingToDoItem() {
        assertNull(repository.findOne(20000L));
    }

    @Test
    public void testCanFindSingleExistingToDoItem() {
        ToDoItem toDoItem = persistToDoItem("Buy milk");
        assertNotNull(repository.findOne(toDoItem.getId()));
    }

    @Test
    public void testCanGetMultipleExistingToDoItem() {
        ToDoItem toDoItem1 = persistToDoItem("Buy milk");
        ToDoItem toDoItem2 = persistToDoItem("Clean dishes");
        List<Long> ids = new ArrayList<>();
        ids.add(toDoItem1.getId());
        ids.add(toDoItem2.getId());
        assertEquals(repository.findAll(ids).size(),2);
    }

    @Test
    public void testCanSaveNewToDoItem() {
        ToDoItem toDoItem = createToDoItem("Buy milk");
        assertNull(toDoItem.getId());
        repository.save(toDoItem);
        assertNotNull(toDoItem.getId());
    }

    @Test
    public void testCanDeleteExistingToDoItem() {
        ToDoItem toDoItem = persistToDoItem("Buy milk");
        assertNotNull(toDoItem.getId());
        repository.delete(toDoItem);
        assertFalse(repository.exists(toDoItem.getId()));
    }

    @Test
    public void testCanUpdateExistingToDoItem() {
        ToDoItem toDoItem = persistToDoItem("Buy milk");
        assertNotNull(toDoItem.getId());
        toDoItem.setName("Clean dishes");
        repository.save(toDoItem);
        assertEquals(repository.findOne(toDoItem.getId()).getName(), "Clean dishes");
    }

    private ToDoItem persistToDoItem(String name) {
        ToDoItem toDoItem = createToDoItem(name);
        entityManager.persist(toDoItem);
        return toDoItem;
    }

    private ToDoItem createToDoItem(String name) {
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setName(name);
        return toDoItem;
    }
}

