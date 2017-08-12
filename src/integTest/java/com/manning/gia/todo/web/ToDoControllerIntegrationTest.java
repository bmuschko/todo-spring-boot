package com.manning.gia.todo.web;

import com.manning.gia.todo.model.ToDoItem;
import com.manning.gia.todo.repository.ToDoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(ToDoController.class)
public class ToDoControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ToDoRepository toDoRepository;

    @Test
    public void testIndexRedirectsToListPage() throws Exception {
        mvc.perform(get("/")).andExpect(redirectedUrl("/all"));
        verifyZeroInteractions(toDoRepository);
    }

    @Test
    public void testCanGetAllItems() throws Exception {
        ToDoItem toDoItem1 = createToDoItem(123L, "Buy milk", true);
        ToDoItem toDoItem2 = createToDoItem(456L, "Wash dishes", false);
        ToDoItem toDoItem3 = createToDoItem(789L, "Go shopping", true);
        List<ToDoItem> items = new ArrayList<>();
        items.add(toDoItem1);
        items.add(toDoItem2);
        items.add(toDoItem3);
        ToDoController.ToDoListStats stats = new ToDoController.ToDoListStats();
        stats.setActive(1);
        stats.setCompleted(2);
        given(toDoRepository.findAll()).willReturn(items);
        verifyNoMoreInteractions(toDoRepository);
        mvc.perform(get("/all"))
                .andExpect(model().attribute("toDoItems", items))
                .andExpect(model().attribute("stats", stats))
                .andExpect(model().attribute("filter", "all"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/WEB-INF/jsp/todo-list.jsp"));
    }

    @Test
    public void testCanGetActiveItems() throws Exception {
        ToDoItem toDoItem1 = createToDoItem(123L, "Buy milk", true);
        ToDoItem toDoItem2 = createToDoItem(456L, "Wash dishes", false);
        ToDoItem toDoItem3 = createToDoItem(789L, "Go shopping", true);
        List<ToDoItem> items = new ArrayList<>();
        items.add(toDoItem1);
        items.add(toDoItem2);
        items.add(toDoItem3);
        List<ToDoItem> activeItems = new ArrayList<>();
        activeItems.add(toDoItem2);
        ToDoController.ToDoListStats stats = new ToDoController.ToDoListStats();
        stats.setActive(1);
        stats.setCompleted(2);
        given(toDoRepository.findAll()).willReturn(items);
        verifyNoMoreInteractions(toDoRepository);
        mvc.perform(get("/active"))
                .andExpect(model().attribute("toDoItems", activeItems))
                .andExpect(model().attribute("stats", stats))
                .andExpect(model().attribute("filter", "active"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/WEB-INF/jsp/todo-list.jsp"));
    }

    @Test
    public void testCanGetCompletedItems() throws Exception {
        ToDoItem toDoItem1 = createToDoItem(123L, "Buy milk", true);
        ToDoItem toDoItem2 = createToDoItem(456L, "Wash dishes", false);
        ToDoItem toDoItem3 = createToDoItem(789L, "Go shopping", true);
        List<ToDoItem> items = new ArrayList<>();
        items.add(toDoItem1);
        items.add(toDoItem2);
        items.add(toDoItem3);
        List<ToDoItem> activeItems = new ArrayList<>();
        activeItems.add(toDoItem1);
        activeItems.add(toDoItem3);
        ToDoController.ToDoListStats stats = new ToDoController.ToDoListStats();
        stats.setActive(1);
        stats.setCompleted(2);
        given(toDoRepository.findAll()).willReturn(items);
        verifyNoMoreInteractions(toDoRepository);
        mvc.perform(get("/completed"))
                .andExpect(model().attribute("toDoItems", activeItems))
                .andExpect(model().attribute("stats", stats))
                .andExpect(model().attribute("filter", "active"))
                .andExpect(status().isOk())
                .andExpect(forwardedUrl("/WEB-INF/jsp/todo-list.jsp"));
    }

    @Test
    public void testCanInsertItem() throws Exception {
        ToDoItem toDoItem = createToDoItem(null, "Buy milk", false);
        mvc.perform(post("/insert")
                .param("name", "Buy milk")
                .param("filter", "/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/all"));
        verify(toDoRepository, times(1)).save(toDoItem);
    }

    @Test
    public void testCanUpdateItemIfFound() throws Exception {
        ToDoItem toDoItem = createToDoItem(123L, "Buy milk", false);
        given(toDoRepository.findOne(123L)).willReturn(toDoItem);
        mvc.perform(post("/update")
                .param("id", "123")
                .param("name", "Wash dishes")
                .param("filter", "/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/all"));
        toDoItem.setName("Wash dishes");
        verify(toDoRepository, times(1)).save(toDoItem);
    }

    @Test
    public void testIgnoresUpdateIfItemNotFound() throws Exception {
        given(toDoRepository.findOne(123L)).willReturn(null);
        verifyNoMoreInteractions(toDoRepository);
        mvc.perform(post("/update")
                .param("id", "123")
                .param("name", "Wash dishes")
                .param("filter", "/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/all"));
    }

    @Test
    public void testCanDeleteItemIfFound() throws Exception {
        ToDoItem toDoItem = createToDoItem(123L, "Buy milk", false);
        given(toDoRepository.findOne(123L)).willReturn(toDoItem);
        mvc.perform(post("/delete")
                .param("id", "123")
                .param("filter", "/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/all"));
        verify(toDoRepository, times(1)).delete(toDoItem);
    }

    @Test
    public void testIgnoresDeleteIfItemNotFound() throws Exception {
        given(toDoRepository.findOne(123L)).willReturn(null);
        verifyNoMoreInteractions(toDoRepository);
        mvc.perform(post("/delete")
                .param("id", "123")
                .param("filter", "/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/all"));
    }

    @Test
    public void testCanToggleStatusForItemIfFound() throws Exception {
        ToDoItem toDoItem = createToDoItem(123L, "Buy milk", false);
        given(toDoRepository.findOne(123L)).willReturn(toDoItem);
        mvc.perform(post("/toggleStatus")
                .param("id", "123")
                .param("toggle", "true")
                .param("filter", "/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/all"));
        toDoItem.setCompleted(true);
        verify(toDoRepository, times(1)).save(toDoItem);
    }

    @Test
    public void testIgnoresToggleStatusIfItemNotFound() throws Exception {
        given(toDoRepository.findOne(123L)).willReturn(null);
        verifyNoMoreInteractions(toDoRepository);
        mvc.perform(post("/toggleStatus")
                .param("id", "123")
                .param("toggle", "true")
                .param("filter", "/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/all"));
    }

    @Test
    public void testClearAllCompletedItems() throws Exception {
        ToDoItem toDoItem1 = createToDoItem(123L, "Buy milk", true);
        ToDoItem toDoItem2 = createToDoItem(456L, "Wash dishes", false);
        ToDoItem toDoItem3 = createToDoItem(789L, "Go shopping", true);
        List<ToDoItem> items = new ArrayList<>();
        items.add(toDoItem1);
        items.add(toDoItem2);
        items.add(toDoItem3);
        given(toDoRepository.findAll()).willReturn(items);
        mvc.perform(post("/clearCompleted")
                .param("filter", "/all"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/all"));
        verify(toDoRepository, times(1)).delete(toDoItem1);
        verify(toDoRepository, never()).delete(toDoItem2);
        verify(toDoRepository, times(1)).delete(toDoItem3);
    }

    private ToDoItem createToDoItem(Long id, String name, boolean completed) {
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setId(id);
        toDoItem.setName(name);
        toDoItem.setCompleted(completed);
        return toDoItem;
    }
}
