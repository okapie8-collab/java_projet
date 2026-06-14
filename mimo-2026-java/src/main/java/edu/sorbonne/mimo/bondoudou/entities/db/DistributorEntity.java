package edu.sorbonne.mimo.bondoudou.entities.db;

import jakarta.persistence.*;

@Entity
@Table(name = "distributors")
public class DistributorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "country")
    private String country;     // typical extra attribute

    public DistributorEntity() {}

    public DistributorEntity(String name, String country) {
        this.name = name;
        this.country = country;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}