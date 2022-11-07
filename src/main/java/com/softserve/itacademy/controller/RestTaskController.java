
package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class RestTaskController {
    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;

    public RestTaskController(TaskService taskService, ToDoService todoService, StateService stateService) {
        this.taskService = taskService;
        this.todoService = todoService;
        this.stateService = stateService;

    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == @toDoServiceImpl.readById(#todoId).owner.id")
    @GetMapping("/todo/{id}")
    public /*List<Map<String, String>>*/ ResponseEntity list(@PathVariable String id) {
        try {
            List<Map<String, String>> strings = new ArrayList<>();
            for (Task actual : taskService.getByTodoId(Integer.parseInt(id))) {
                Map<String, String> map = new HashMap<>();
                map.put("task_id", String.valueOf(actual.getId()));
                map.put("task", actual.getName());
                map.put("priority", actual.getPriority().toString());
                map.put("state", actual.getState().getName());

                strings.add(map);
            }
            return new ResponseEntity(strings,HttpStatus.OK);
        }
        catch (Exception e)
        {
            return  ResponseEntity.notFound().build();
        }

    }

    @GetMapping("{id}")
    public /*Map<String, String>*/ ResponseEntity getOne(@PathVariable String id) {
        try {
            Task actual = taskService.readById(Integer.parseInt(id));
            Map<String, String> string = new HashMap<>();
            string.put("task_id", String.valueOf(actual.getId()));
            string.put("task", actual.getName());
            string.put("priority", actual.getPriority().toString());
            string.put("state_id", String.valueOf(actual.getState().getId()));
            string.put("todo_id", String.valueOf(actual.getTodo().getId()));
            return getTaskMap(actual, HttpStatus.OK);
        }
        catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping("/todo/{todo_id}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public /*Map<String, String>*/ ResponseEntity create(@PathVariable String todo_id, @RequestBody Map<String, String> task) {
        try {
            TaskDto taskDto = new TaskDto(Long.parseLong(task.get("task_id")), task.get("task"), task.get("priority"), Long.parseLong(task.get("todo_id")), Long.parseLong(task.get("state_id")));
            Task task1 = TaskTransformer.convertToEntity(taskDto,
                    todoService.readById(taskDto.getTodoId()),
                    stateService.getByName("New"));
            return getTaskMap(taskService.create(task1),HttpStatus.CREATED);
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("{id}")
    public ResponseEntity update(@PathVariable String id, @RequestBody Map<String, String> editedMap) {
        try {
            TaskDto taskDto = new TaskDto(Long.parseLong(editedMap.get("task_id")), editedMap.get("task"), editedMap.get("priority"), Long.parseLong(editedMap.get("todo_id")), Long.parseLong(editedMap.get("state_id")));
            Task task = TaskTransformer.convertToEntity(
                    taskDto,
                    todoService.readById(taskDto.getTodoId()),
                    stateService.readById(taskDto.getStateId())
            );
            Task oldTask = taskService.readById(Integer.valueOf(id));
            oldTask.setName(task.getName());
            oldTask.setState(task.getState());
            oldTask.setPriority(task.getPriority());
            taskService.update(oldTask);

            return getTaskMap(oldTask,HttpStatus.OK);
        }
        catch (Exception e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity delete(@PathVariable String id) {

        try {
            taskService.delete(Integer.parseInt(id));
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }
    private ResponseEntity getTaskMap(Task enteredTask, HttpStatus httpStatus) {
        Map<String, String> editedMap = new LinkedHashMap<>();
        editedMap.put("id", String.valueOf(enteredTask.getId()));
        editedMap.put("name", enteredTask.getName());
        editedMap.put("priority", enteredTask.getPriority().toString());
        editedMap.put("state", enteredTask.getState().getName());
        editedMap.put("todo_id", String.valueOf(enteredTask.getTodo().getId()));
        return new ResponseEntity(editedMap, httpStatus);
    }

}