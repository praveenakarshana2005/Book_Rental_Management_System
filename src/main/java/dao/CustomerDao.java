package dao;

import model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    Customer save(Customer customer) throws Exception;

    boolean update(Customer customer) throws Exception;

    boolean delete(int customer) throws Exception;

    Optional<Customer> findById(int id) throws Exception;

    List<Customer> findAll() throws Exception;

    List<Customer> search(String q) throws Exception;



}
