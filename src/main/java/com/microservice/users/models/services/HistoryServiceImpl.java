package com.microservice.users.models.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservice.users.models.dao.IHistoryDao;
import com.microservice.users.models.entity.History;

@Service
public class HistoryServiceImpl implements IHistoryService {

	@Autowired
	IHistoryDao historyDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<History> findAll() {
		return (List<History>) historyDao.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public History findById(Long id) {
		return historyDao.findById(id).orElse(null);
	}

	@Override
	public History save(History history) {
		return historyDao.save(history);
	}

	@Override
	public void delete(Long id) {
		historyDao.deleteById(id);
	}

}
