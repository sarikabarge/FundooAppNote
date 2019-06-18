package com.bridgeit.fundoo.note.service;

import java.util.List;

import com.bridgeit.fundoo.note.dto.NoteDTO;
import com.bridgeit.fundoo.note.model.Note;
import com.bridgeit.fundoo.response.Response;

public interface NoteService {

	public Response createNote(NoteDTO notedto,String token);
	
	public Response updateNote(NoteDTO noteDto, String token, long noteId);
	
	public Response retrieveNote(String token, long noteId);
	
	public Response deleteNote(String token, long noteId);
	
	public Response deleteNotePermenantly(String token, long noteId);
	
	public List<Note> getAllNote(String token);

	List<Note> restoreTrashNote(String token);

	List<Note> restoreArchieveNote(String token);


}
