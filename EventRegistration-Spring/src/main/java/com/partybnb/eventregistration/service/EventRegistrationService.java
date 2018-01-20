package com.partybnb.eventregistration.service;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.partybnb.eventregistration.model.Event;
import com.partybnb.eventregistration.model.Participant;
import com.partybnb.eventregistration.model.Registration;
import com.partybnb.eventregistration.model.RegistrationManager;
import com.partybnb.eventregistration.persistence.PersistenceXStream;

@Service
public class EventRegistrationService {
	private RegistrationManager rm;

	public EventRegistrationService(RegistrationManager rm) {
		this.rm = rm;
	}

	private boolean checkIfEmptyOrNull(String name) {
		return name == null || name.trim().equals("");
	}

	public Participant createParticipant(String name) throws InvalidInputException {
		if (checkIfEmptyOrNull(name))
			throw new InvalidInputException("Participant name cannot be empty!");
		Participant p = new Participant(name);
		rm.addParticipant(p);
		PersistenceXStream.saveToXMLwithXStream(rm);
		return p;
	}

	public Event createEvent(String name, Date date, Time startTime, Time endTime) throws InvalidInputException {
		if(name == null || date == null || startTime == null || endTime == null) 
			throw new InvalidInputException("Event name cannot be empty! Event date cannot be empty! Event start time cannot be empty! Event end time cannot be empty!");
		else if (name.trim().contentEquals(""))
			throw new InvalidInputException("Event name cannot be empty!");				
		else if (startTime.compareTo(endTime) > 0)
			throw new InvalidInputException("Event end time cannot be before event start time!");
		
		Event e = new Event(name, date, startTime, endTime);
		rm.addEvent(e);
		PersistenceXStream.saveToXMLwithXStream(rm);

		return e;
	}

	public Registration register(Participant p, Event e) throws InvalidInputException {
		if (p == null || e == null)
			throw new InvalidInputException("Participant needs to be selected for registration! Event needs to be selected for registration!");
		else if(!rm.getParticipants().contains(p) ||  !rm.getEvents().contains(e)) {
			throw new InvalidInputException("Participant does not exist! Event does not exist!");			
		}
		
		Registration r = new Registration(p, e);
		rm.addRegistration(r);
		PersistenceXStream.saveToXMLwithXStream(rm);

		return r;
	}

	public List<Event> findAllEvents() {
		return rm.getEvents();
	}

	public List<Participant> findAllParticipants() {
		return rm.getParticipants();
	}

	public List<Event> getEventsForParticipant(Participant p) {
		List<Event> events = new ArrayList<>();
		for (Registration r : rm.getRegistrations()) {
			if (r.getParticipant().getName().contentEquals(p.getName()))
				events.add(r.getEvent());
		}

		return events;
	}

	public Participant findParticipant(String name) throws InvalidInputException {
		Participant p = null;
		for(Participant tmp: rm.getParticipants()) {
			if(tmp.getName().contentEquals(name)) {
				p = tmp;
				break;
			}
		}
		
		if (p == null)
			throw new InvalidInputException("Participant was not found");
		return p;
	}

	public Event findEvent(String name) throws InvalidInputException {
		Event e = null;
		for(Event tmp: rm.getEvents()) {
			if(tmp.getName().contentEquals(name)) {
				e = tmp;
				break;
			}
		}
		
		if (e == null)
			throw new InvalidInputException("Event was not found");
		return e;
	}

}
