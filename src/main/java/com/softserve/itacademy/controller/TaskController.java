/*
package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;

    public TaskController(TaskService taskService, ToDoService todoService, StateService stateService) {
        this.taskService = taskService;
        this.todoService = todoService;
        this.stateService = stateService;
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == @toDoServiceImpl.readById(#todoId).owner.id")
    @GetMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model) {
        model.addAttribute("task", new TaskDto());
        model.addAttribute("todo", todoService.readById(todoId));
        model.addAttribute("priorities", Priority.values());
        return "create-task";
    }

//    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == @toDoServiceImpl.readById(#todoId).owner.id")
    @PostMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", Priority.values());
            return "create-task";
        }
        Task task = TaskTransformer.convertToEntity(
                taskDto,
                todoService.readById(taskDto.getTodoId()),
                stateService.getByName("New")
        );
        taskService.create(task);
        return "redirect:/todos/" + todoId + "/tasks";
    }


//    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == @toDoServiceImpl.readById(#todoId).owner.id")
    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model) {
        TaskDto taskDto = TaskTransformer.convertToDto(taskService.readById(taskId));
        model.addAttribute("task", taskDto);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("states", stateService.getAll());
        return "update-task";
    }


//    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == @toDoServiceImpl.readById(#todoId).owner.id")
    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task")TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("states", stateService.getAll());
            return "update-task";
        }
        Task task = TaskTransformer.convertToEntity(
                taskDto,
                todoService.readById(taskDto.getTodoId()),
                stateService.readById(taskDto.getStateId())
        );
        taskService.update(task);
        return "redirect:/todos/" + todoId + "/tasks";
    }


//    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == @toDoServiceImpl.readById(#todoId).owner.id")
    @GetMapping("/{task_id}/delete/todos/{todo_id}")
    public String delete(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId) {
        taskService.delete(taskId);
        return "redirect:/todos/" + todoId + "/tasks";
    }
}
*/
package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.Priority;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;

    public TaskController(TaskService taskService, ToDoService todoService, StateService stateService) {
        this.taskService = taskService;
        this.todoService = todoService;
        this.stateService = stateService;

    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == @toDoServiceImpl.readById(#todoId).owner.id")
    @GetMapping("/todo/{id}")
    public List<Map<String,String>> list(@PathVariable String id)
    {
        List<Map<String,String>> strings=new ArrayList<>();
        for(Task actual: taskService.getByTodoId(Integer.parseInt(id)))
        {   Map<String,String> map=new HashMap<>();
            map.put("task_id",String.valueOf(actual.getId()));
            map.put("task",actual.getName());
            map.put("priority",actual.getPriority().toString());
            map.put("state",actual.getState().getName());

            strings.add(map);
        }
        return strings;
    }
    @GetMapping("{id}")
    public Map<String,String> getOne(@PathVariable String id)
    {
        Task actual=taskService.readById(Integer.parseInt(id));
        Map<String,String> string =new HashMap<>();
        string.put("task_id",String.valueOf(actual.getId()));
        string.put("task",actual.getName());
        string.put("priority",actual.getPriority().toString());
        string.put("state_id",String.valueOf(actual.getState().getId()));
        string.put("todo_id",String.valueOf(actual.getTodo().getId()));
        return string;
    }

    @PostMapping("/todo/{todo_id}")
    public Map<String,String> create(@PathVariable String todo_id, @RequestBody Map<String,String> task)
    {
        TaskDto taskDto=new TaskDto(Long.parseLong(task.get("task_id")),task.get("task"),task.get("priority"),Long.parseLong(task.get("todo_id")),Long.parseLong(task.get("state_id") ));
        Task task1=TaskTransformer.convertToEntity(taskDto,
                todoService.readById(taskDto.getTodoId()),
                stateService.getByName("New"));
        taskService.create(task1);
        Map<String,String> map=new HashMap<>();
        map.put("task_id",String.valueOf(task1.getId()));
        map.put("task",task1.getName());
        map.put("priority",task1.getPriority().toString());
        map.put("state_id",String.valueOf(task1.getState().getId()));
        map.put("todo_id",String.valueOf(task1.getTodo().getId()));
        return map;
    }

    @PutMapping("{id}")
    public Map<String,String> update(@PathVariable String id, @RequestBody Map<String,String> editedMap)
    {
        TaskDto taskDto=new TaskDto(Long.parseLong(editedMap.get("task_id")),editedMap.get("task"),editedMap.get("priority"),Long.parseLong(editedMap.get("todo_id")),Long.parseLong(editedMap.get("state_id") ));
        Task task = TaskTransformer.convertToEntity(
                taskDto,
                todoService.readById(taskDto.getTodoId()),
                stateService.readById(taskDto.getStateId())
        );
        Task oldTask=taskService.readById(Integer.valueOf(id));
        Map<String,String> oldMap=new HashMap<>();
        oldMap.put("task_id",id);
        oldMap.put("task",oldTask.getName());
        oldMap.put("priority",oldTask.getPriority().toString());
        oldMap.put("state_id",String.valueOf(oldTask.getState().getId()));
        oldMap.put("todo_id",String.valueOf(oldTask.getTodo().getId()));

        oldMap.putAll(editedMap);
        taskService.update(task);

        return oldMap;
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id)
    {
        taskService.delete(Integer.parseInt(id));

    }


    //    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == @toDoServiceImpl.readById(#todoId).owner.id")
  /*  @PostMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task") TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", Priority.values());
            return "create-task";
        }
        Task task = TaskTransformer.convertToEntity(
                taskDto,
                todoService.readById(taskDto.getTodoId()),
                stateService.getByName("New")
        );
        taskService.create(task);
        return "redirect:/todos/" + todoId + "/tasks";
    }


    //    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == @toDoServiceImpl.readById(#todoId).owner.id")
    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model) {
        TaskDto taskDto = TaskTransformer.convertToDto(taskService.readById(taskId));
        model.addAttribute("task", taskDto);
        model.addAttribute("priorities", Priority.values());
        model.addAttribute("states", stateService.getAll());
        return "update-task";
    }


    //    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == @toDoServiceImpl.readById(#todoId).owner.id")
    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId, Model model,
                         @Validated @ModelAttribute("task")TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", Priority.values());
            model.addAttribute("states", stateService.getAll());
            return "update-task";
        }
        Task task = TaskTransformer.convertToEntity(
                taskDto,
                todoService.readById(taskDto.getTodoId()),
                stateService.readById(taskDto.getStateId())
        );
        taskService.update(task);
        return "redirect:/todos/" + todoId + "/tasks";
    }


    //    @PreAuthorize("hasRole('ROLE_ADMIN') or authentication.principal.id == @toDoServiceImpl.readById(#todoId).owner.id")
    @GetMapping("/{task_id}/delete/todos/{todo_id}")
    public String delete(@PathVariable("task_id") long taskId, @PathVariable("todo_id") long todoId) {
        taskService.delete(taskId);
        return "redirect:/todos/" + todoId + "/tasks";
    }*/
}
