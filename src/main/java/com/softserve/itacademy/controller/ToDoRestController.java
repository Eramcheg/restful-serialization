package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/todos")
public class ToDoRestController{

    private final ToDoService todoService;
    private final TaskService taskService;
    private final UserService userService;

    public ToDoRestController(ToDoService todoService, TaskService taskService, UserService userService) {
        this.todoService = todoService;
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping("/create/users/{owner_id}")
    public ResponseEntity create(@PathVariable("owner_id") long ownerId, @RequestBody Map<String, String> todoInput) {

        try {
            ToDo todo = new ToDo();
            todo.setTitle(todoInput.get("title"));
            todo.setCreatedAt(LocalDateTime.now());
            todo.setOwner(userService.readById(ownerId));
            todoService.create(todo);
            return getTodoMap(todo, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/tasks")
    public ResponseEntity read(@PathVariable long id) {

        try {
            if (todoService.readById(id) == null){
                return ResponseEntity.notFound().build();
            }
            List<Task> tasks = taskService.getByTodoId(id);
            Map<String, String> tasksList = new LinkedHashMap<>();
            for (Task task : tasks) {
                tasksList.put("id", String.valueOf(task.getId()));
                tasksList.put("name", task.getName());
                tasksList.put("priority", task.getPriority().toString());
                tasksList.put("todo_id", String.valueOf(task.getTodo().getId()));
                tasksList.put("state", task.getState().getName().toUpperCase());
            }
            return new ResponseEntity(tasksList, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{todo_id}/update/users/{owner_id}")
    public ResponseEntity update(@PathVariable("todo_id") long todoId, @RequestBody Map<String, String> editedTodo) {
        try {
            ToDo todo = todoService.readById(todoId);
            todo.setTitle(editedTodo.get("title"));
            todoService.update(todo);
            return getTodoMap(todo, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{todo_id}/delete/users/{owner_id}")
    public ResponseEntity delete(@PathVariable("todo_id") long todoId, @PathVariable("owner_id") long ownerId) {
        try {
            todoService.delete(todoId);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all/users/{user_id}")
    public ResponseEntity getAll(@PathVariable("user_id") long userId) {
        try {
            List<ToDo> todos = todoService.getByUserId(userId);
            List<Map<String, String>> todosList = new ArrayList<>();
            Map<String, String> tempUser;
            for (ToDo todo : todos) {
                tempUser = new LinkedHashMap<>();
                tempUser.put("id", String.valueOf(todo.getId()));
                tempUser.put("title", todo.getTitle());
                tempUser.put("created_at", todo.getCreatedAt().toString());
                tempUser.put("owner_id", String.valueOf(todo.getOwner().getId()));
                todosList.add(tempUser);
            }
            return new ResponseEntity(todosList, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/add")
    public ResponseEntity addCollaborator(@PathVariable long id, @RequestBody Map<String, String> collaboratorInput, Authentication authentication) {
        try {
            ToDo todo = todoService.readById(id);
            List<User> collaborators = todo.getCollaborators();
            Long collaboratorId = Long.valueOf(collaboratorInput.get("collaborator_id"));
            if (collaborators.contains(userService.readById(collaboratorId))){
                return new ResponseEntity(HttpStatus.CONFLICT);
            }
            else if (!authentication.name().equals(todo.getOwner().getEmail())){
                return new ResponseEntity(HttpStatus.FORBIDDEN);
            }
            collaborators.add(userService.readById(collaboratorId));
            todo.setCollaborators(collaborators);
            todoService.update(todo);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/remove")
    public ResponseEntity removeCollaborator(@PathVariable long id, @RequestBody Map<String, String> collaboratorInput) {
        try {
            ToDo todo = todoService.readById(id);
            List<User> collaborators = todo.getCollaborators();
            Long collaboratorId = Long.valueOf(collaboratorInput.get("collaborator_id"));
            collaborators.remove(userService.readById(collaboratorId));
            todo.setCollaborators(collaborators);
            todoService.update(todo);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity getTodoMap(ToDo enteredTodo, HttpStatus httpStatus) {
        Map<String, String> editedMap = new LinkedHashMap<>();
        editedMap.put("id", String.valueOf(enteredTodo.getId()));
        editedMap.put("title", enteredTodo.getTitle());
        editedMap.put("created_at", enteredTodo.getCreatedAt().toString());
        editedMap.put("owner_id", String.valueOf(enteredTodo.getOwner().getId()));
        return new ResponseEntity(editedMap, httpStatus);
    }
}