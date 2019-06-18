package com.bridgeit.fundoo.note.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.crypto.Data;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bridgeit.fundoo.exception.UserException;
import com.bridgeit.fundoo.note.dto.NoteDTO;
import com.bridgeit.fundoo.note.model.Note;
import com.bridgeit.fundoo.note.repository.NoteRepository;
import com.bridgeit.fundoo.response.Response;
import com.bridgeit.fundoo.user.model.User;
import com.bridgeit.fundoo.user.repository.UserRepository;
import com.bridgeit.fundoo.utility.ResponseHelper;
import com.bridgeit.fundoo.utility.TokenUtil;


@Service("notesService")
@PropertySource("classpath:message.properties")
public class NoteServiceImp implements NoteService{

	@Autowired
	private TokenUtil userToken;
	
	@Autowired
	private NoteRepository noterepository;
	
	@Autowired
	private UserRepository userrepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private Environment environment;
	
	@Override
	public Response createNote(NoteDTO notedto,String token)
	{
		long id = userToken.decodeToken(token);

		if (notedto.getTitle().isEmpty() && notedto.getDescription().isEmpty()) {

			throw new UserException(-5, "Title and description are empty");

		}
		Note note = modelMapper.map(notedto, Note.class);
		Optional<User> user = userrepository.findById(id);
		note.setUserId(id);
		note.setCreated(LocalDateTime.now());
		note.setModified(LocalDateTime.now());
		user.get().getNotes().add(note);
		noterepository.save(note);
		userrepository.save(user.get());

		Response response = ResponseHelper.statusResponse(100,
				environment.getProperty("status.notes.createdSuccessfull"));
		return response;
				
		
	}
	
	@Override
	public Response updateNote(NoteDTO noteDto, String token, long noteId) {
		if (noteDto.getTitle().isEmpty() && noteDto.getDescription().isEmpty()) {
			throw new UserException(-5, "Title and description are empty");
		}

		long id = userToken.decodeToken(token);
		Note note = noterepository.findByUserIdAndNoteId(id, noteId);
		note.setTitle(noteDto.getTitle());
		note.setDescription(noteDto.getDescription());
		note.setModified(LocalDateTime.now());
		noterepository.save(note);

		Response response = ResponseHelper.statusResponse(200,
				environment.getProperty("status.notes.updatedSuccessfull"));
		return response;
	}
	
	@Override
	public Response retrieveNote(String token, long noteId) {

		long id = userToken.decodeToken(token);
		Note note = noterepository.findByUserIdAndNoteId(id, noteId);
		String title = note.getTitle();
		System.out.println(title);

		String description = note.getDescription();
		System.out.println(description);

		Response response = ResponseHelper.statusResponse(300, "retrieved successfully");
		return response;
	}
	
	public Response deleteNote(String token, long noteId) {
		long id = userToken.decodeToken(token);

		Note note = noterepository.findByUserIdAndNoteId(id, noteId);

		if (note.isTrash() == false) {
			note.setTrash(true);
			note.setModified(LocalDateTime.now());
			noterepository.save(note);
			Response response = ResponseHelper.statusResponse(100, environment.getProperty("status.note.trashed"));
			return response;
		}

		Response response = ResponseHelper.statusResponse(100, environment.getProperty("status.note.trashError"));
		return response;
	}
	
	public Response deleteNotePermenantly(String token, long noteId) {

		long id = userToken.decodeToken(token);

		Optional<User> user = userrepository.findById(id);
		Note note = noterepository.findByUserIdAndNoteId(id, noteId);

		if (note.isDeletePermantely() == false) {
			note.setDeletePermantely(true);
			note.setModified(LocalDateTime.now());
			noterepository.save(note);
			Response response = ResponseHelper.statusResponse(200, environment.getProperty("status.note.deleted"));
			return response;
		} else {
			Response response = ResponseHelper.statusResponse(100, environment.getProperty("status.note.noteDeleted"));
			return response;
		}
	}
	
	public List<Note> getAllNote(String token)
	{
		long userId=userToken.decodeToken(token);
		Optional<User> user = userrepository.findById(userId);
		
		if(!user.isPresent())
		{
			throw new UserException("invalid input");
		}
		
		List<Note> notes=noterepository.findByUserId(userId);
		List<Note> listnote= new ArrayList<>();
		
		for(Note note:notes)
		{
			Note notedto = modelMapper.map(note, Note.class);
			listnote.add(notedto);
		}
		
		return listnote;
	}
	
	@Override
	public List<Note> restoreTrashNote(String token)
	{
		long userId=userToken.decodeToken(token);
		User user = userrepository.findById(userId).orElseThrow(()->new UserException("Note are not available"));
		
		List<Note> trashnote=user.getNotes().stream().filter(data -> data.isTrash() == true).collect(Collectors.toList());
		return trashnote;
	}
	
	
}
