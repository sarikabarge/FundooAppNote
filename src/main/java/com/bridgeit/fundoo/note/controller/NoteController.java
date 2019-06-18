package com.bridgeit.fundoo.note.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgeit.fundoo.note.dto.NoteDTO;
import com.bridgeit.fundoo.note.model.Note;
import com.bridgeit.fundoo.note.service.NoteService;
import com.bridgeit.fundoo.response.Response;

@RestController
@RequestMapping("/user")
@PropertySource("classpath:message.properties")
public class NoteController {

	@Autowired
	NoteService noteservice;
	@PostMapping("/createNote")
	public ResponseEntity<Response> creatNote(@RequestBody NoteDTO notedto,@RequestHeader String token)
   	{
		System.out.println("NotesController.creatingNote()");
		Response responseStatus = noteservice.createNote(notedto,token);
		return new ResponseEntity<Response>(responseStatus, HttpStatus.OK);
		
	}
	

	@PutMapping("/updateNote")
	public ResponseEntity<Response> updatingNote(@RequestBody NoteDTO noteDto, @RequestHeader String token,
			@RequestParam long noteId) {

		Response responseStatus = noteservice.updateNote(noteDto, token, noteId);
		return new ResponseEntity<Response>(responseStatus, HttpStatus.ACCEPTED);
	}

	@PutMapping("/retrieveNote")
	public ResponseEntity<Response> retrievingNote(@RequestHeader String token, @RequestParam long noteId) {

		Response responseStatus = noteservice.retrieveNote(token, noteId);
		return new ResponseEntity<Response>(responseStatus, HttpStatus.OK);
	}
	
	@PutMapping("/deleteNote")
	public ResponseEntity<Response> deletingNote(@RequestHeader String token, @RequestParam long noteId) {
		Response responseStatus = noteservice.deleteNote(token, noteId);
		return new ResponseEntity<Response>(responseStatus, HttpStatus.OK);
	}
	
	@PutMapping("/deleteNotePermenantly")
	public ResponseEntity<Response> PermenantlydeleteNote(@RequestHeader String token, @RequestParam long noteId) {
		Response responseStatus = noteservice.deleteNotePermenantly(token, noteId);
		return new ResponseEntity<Response>(responseStatus, HttpStatus.OK);
	}
	
	
	@GetMapping("/getAllNote")
	List<Note> getNote(@RequestHeader String token)
	{
		List<Note> listnote=noteservice.getAllNote(token);
		return listnote;
	}
	
	@GetMapping("/getTrashNote")
	List<Note> getTrashNote(@RequestHeader String token)
	{
		List<Note> listTrashNote = noteservice.restoreTrashNote(token);
		return listTrashNote;
	}
	
	
	
}
