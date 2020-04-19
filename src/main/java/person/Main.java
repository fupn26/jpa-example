package person;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.github.javafaker.Faker;
import legoset.model.LegoSet;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;

import java.time.Year;
import java.time.ZoneId;

@Log4j2
public class Main {
    private static Faker faker = new Faker();

    private static Person randomPerson() {
        Person person = Person.builder()
                .name(faker.name().fullName())
                .dob(faker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .gender(faker.options().option(Person.Gender.class))
                .address(Address.builder()
                        .country(faker.address().country())
                        .state(faker.address().state())
                        .city(faker.address().city())
                        .streetAddress(faker.address().streetAddress())
                        .zip(faker.address().zipCode())
                        .build())
                .email(faker.internet().emailAddress())
                .profession(faker.company().profession())
                .build();
        return person;
    }

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa-example");
        EntityManager em = emf.createEntityManager();

        int numberOfIterations = Integer.parseInt(args[0]);
        try {
            em.getTransaction().begin();
            for (int i = 0; i < numberOfIterations; ++i) {
                em.persist(randomPerson());
            }
            em.getTransaction().commit();

            em.createQuery("SELECT l FROM Person l ORDER BY l.id", Person.class).getResultList().forEach(log::info);
        } finally {
            em.close();
            emf.close();
        }

    }
}
