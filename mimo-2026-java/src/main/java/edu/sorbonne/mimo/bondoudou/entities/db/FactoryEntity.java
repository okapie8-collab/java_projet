package edu.sorbonne.mimo.bondoudou.entities.db;

import jakarta.persistence.*;

@Entity
@Table(name = "factories")
public class FactoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "country")
    private String country;

    @Column(name = "number_of_employees")
    private int numberOfEmployees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distributor_id", nullable = false)
    private DistributorEntity distributor;

    public FactoryEntity() {}

    public FactoryEntity(String name, String country, int numberOfEmployees, DistributorEntity distributor) {
        this.name = name;
        this.country = country;
        this.numberOfEmployees = numberOfEmployees;
        this.distributor = distributor;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public int getNumberOfEmployees() { return numberOfEmployees; }
    public void setNumberOfEmployees(int numberOfEmployees) { this.numberOfEmployees = numberOfEmployees; }
    public DistributorEntity getDistributor() { return distributor; }
    public void setDistributor(DistributorEntity distributor) { this.distributor = distributor; }
}
