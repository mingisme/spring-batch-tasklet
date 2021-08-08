package com.example.springbatchtasklet.batch;

import com.example.springbatchtasklet.model.Person;
import org.springframework.batch.item.ItemProcessor;

import java.util.Locale;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    @Override
    public Person process(Person person) throws Exception {
        person.setFirstName(person.getFirstName().toUpperCase(Locale.ROOT));
        person.setLastName(person.getLastName().toUpperCase(Locale.ROOT));
        return person;
    }
}
