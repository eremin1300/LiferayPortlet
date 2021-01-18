package test.demo.model;

import java.io.Serializable;
import java.util.Date;

public class Vacancy implements Serializable {

    private Long id;
    private String name;
    private Date dateOfPublic;
    private String organization;
    private String salary;      //почему String - потому что часто встречается "По договоренности" или "Не определена".


    public Vacancy(String id, String name, Date dateOfPublic, String organization, String salary) {
        this.name = name;
        this.dateOfPublic = dateOfPublic;
        this.organization = organization;
        this.salary = salary;
        this.id = Long.parseLong(id);
    }

    public Vacancy() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getDateOfPublic() {
        return dateOfPublic;
    }

    public String getOrganization() {
        return organization;
    }

    public String getSalary() {
        return salary;
    }

    @Override
    public String toString() {
        return "test.demo.model.Vacancy{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateOfPublic=" + dateOfPublic +
                ", organization='" + organization + '\'' +
                ", salary='" + salary + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        Vacancy vac = (Vacancy) obj;
        if (vac.getId() == this.getId() && vac.getName().equals(this.getName())
                && vac.getDateOfPublic().equals(this.getDateOfPublic())
                && vac.getOrganization().equals(this.getOrganization()) && vac.getSalary().equals(this.getSalary()))
            return true;
        else return false;
    }
}

