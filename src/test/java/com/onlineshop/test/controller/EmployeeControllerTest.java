package com.onlineshop.test.controller;

import com.onlineshop.test.dto.request.EmployeeRequest;
import com.onlineshop.test.dto.response.EmployeeResponse;
import com.onlineshop.test.exception.EmployeeNotFoundException;
import com.onlineshop.test.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    @DisplayName("getALlEmployees with happy flow")
    void getAllEmployees_ShouldReturnAllEmployees() throws Exception {
        EmployeeResponse employee1 = new EmployeeResponse(1L, "Bob Marley", "Singer", 111111L, "Music and Art", null);
        EmployeeResponse employee2 = new EmployeeResponse(2L, "Billy Jackson", "Mobile Developer", 12500L, "IT", "Ren Han");

        when(employeeService.getAllEmployees()).thenReturn(List.of(employee1, employee2));

        mockMvc.perform(get("/api/employees")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));

        Mockito.verify(employeeService, Mockito.times(1)).getAllEmployees();
    }

    @Test
    @DisplayName("getALlEmployees with empty list")
    void getAllEmployees_ShouldReturnEmptyList() throws Exception {
        List<EmployeeResponse> employeeList = new ArrayList<>();

        when(employeeService.getAllEmployees()).thenReturn(employeeList);

        mockMvc.perform(get("/api/employees")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        Mockito.verify(employeeService, Mockito.times(1)).getAllEmployees();
    }

    @Test
    @DisplayName("GetEmployeeById with happy flow")
    void getEmployeeById_ShouldReturnEmployeeById() throws Exception {
        var emplId = 1L;
        var employeeResponse = new EmployeeResponse(1L, "Arsen", "Comedian", 500L, "Entertainment", "Charlie Chaplin");

        when(employeeService.getEmployeeById(emplId)).thenReturn(employeeResponse);

        mockMvc.perform(get("/api/employees/{id}", emplId)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.name").value("Arsen"))
            .andExpect(jsonPath("$.position").value("Comedian"));

        Mockito.verify(employeeService, Mockito.times(1)).getEmployeeById(emplId);
    }

    @Test
    @DisplayName("getEmployeeById with EmployeeNotFoundException result")
    void getEmployeeById_ShouldReturnEmployeeNotFoundException_WhenEmployeeDoesNotExist() throws Exception {
        var emplId = 1L;

        when(employeeService.getEmployeeById(emplId)).thenThrow(new EmployeeNotFoundException(emplId));

        mockMvc.perform(get("/api/employees/{id}", emplId)
                .contentType("application/json"))
            .andExpect(status().isNotFound());

        Mockito.verify(employeeService, Mockito.times(1)).getEmployeeById(emplId);
    }

    @Test
    @DisplayName("createEmployee with happy flow")
    void createEmployee_ShouldCreateAndReturnEmployeeResponse() throws Exception {
        var employeeRequest = new EmployeeRequest();
        employeeRequest.setName("Neymar");
        employeeRequest.setPosition("Football player");
        employeeRequest.setSalary(2500000L);
        employeeRequest.setDepartmentId(1L);
        employeeRequest.setManagerId(null);

        var employeeResponse = new EmployeeResponse(1L, "Neymar", "Football player", 2500000L, "Sports", null);

        when(employeeService.createEmployee(employeeRequest)).thenReturn(employeeResponse);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employeeRequest)))
            .andExpect(status().isOk());

        Mockito.verify(employeeService, Mockito.times(1)).createEmployee(employeeRequest);
    }

    @Test
    @DisplayName("createEmployee with bad request flow")
    void createEmployee_ShouldThrowEmployeeBadRequest_WhenEmployeeRequestIsBad() throws Exception {
        var employeeRequest = new EmployeeRequest();
        employeeRequest.setName("");
        employeeRequest.setPosition("");
        employeeRequest.setSalary(null);
        employeeRequest.setDepartmentId(null);
        employeeRequest.setManagerId(null);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employeeRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("updateEmployee with happy flow")
    void updateEmployee_ShouldUpdateAndReturnEmployeeResponse() throws Exception {
        var employeeId = 1L;

        var employeeRequest = new EmployeeRequest();
        employeeRequest.setName("Neymar");
        employeeRequest.setPosition("Football manger");
        employeeRequest.setSalary(20000L);
        employeeRequest.setDepartmentId(2L);
        employeeRequest.setManagerId(null);

        var updateResponse = new EmployeeResponse(1L, "Neymar", "Football manger", 20000L, "Sports", null);

        when(employeeService.updateEmployee(employeeId, employeeRequest)).thenReturn(updateResponse);

        mockMvc.perform(put("/api/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employeeRequest)))
            .andExpect(status().isOk());
        Mockito.verify(employeeService, Mockito.times(1)).updateEmployee(employeeId, employeeRequest);
    }

    @Test
    @DisplayName("updateEmployee with exception flow")
    void updateEmployee_ShouldThowEmployeeNotFoundException_WhenEmployeeNotFound() throws Exception {
        var employeeId = 2L;

        var employeeRequest = new EmployeeRequest();
        employeeRequest.setName("Neymar");
        employeeRequest.setPosition("Football manger");
        employeeRequest.setSalary(20000L);
        employeeRequest.setDepartmentId(2L);
        employeeRequest.setManagerId(null);

        when(employeeService.updateEmployee(employeeId, employeeRequest)).thenThrow(EmployeeNotFoundException.class);

        mockMvc.perform(put("/api/employees/{id}", employeeId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(employeeRequest)))
            .andExpect(status().isNotFound());
        Mockito.verify(employeeService, Mockito.times(1)).updateEmployee(employeeId, employeeRequest);

    }

    @Test
    @DisplayName("deleteEmployee with happy flow")
    void deleteEmployee_ShouldDeleteEmployeeAndReturnOk() throws Exception {
        var emplId = 1L;

        Mockito.doNothing().when(employeeService).deleteEmployee(emplId);

        mockMvc.perform(delete("/api/employees/{id}", emplId))
            .andExpect(status().isOk());

        Mockito.verify(employeeService, Mockito.times(1)).deleteEmployee(emplId);
    }

    @Test
    @DisplayName("deleteEmployee with happy flow")
    void deleteEmployee_ShouldThrowEmployeeNotFountException_WhenEmployeeNotFound() throws Exception {
        var emplId = 1L;

        Mockito.doThrow(new EmployeeNotFoundException(emplId)).when(employeeService).deleteEmployee(emplId);

        mockMvc.perform(delete("/api/employees/{id}", emplId))
            .andExpect(status().isNotFound());

        Mockito.verify(employeeService, Mockito.times(1)).deleteEmployee(emplId);
    }


}
