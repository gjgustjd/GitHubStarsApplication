package com.example.githubstars.Activity

import androidx.lifecycle.*
import com.example.githubstars.model.GithubStarsRepository
import com.example.githubstars.model.dto.UserItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(private val repository: GithubStarsRepository) :
    ViewModel() {
    var userList = MutableLiveData<List<UserItem>>()
    var localUserList = MutableLiveData<List<UserItem>>()
    private var currentLocalWord = ""
    var userIdList = repository.getAllUserIdList().asLiveData()

    fun setLocalTargetWord(word: String) {
        currentLocalWord = word
        viewModelScope.launch {
            var userList = repository.getLocalUserList(word).await()
            localUserList.postValue(userList)
        }
    }

    fun setupUserList(word: String = "") {
        viewModelScope.launch {
            var response = repository.getUsersList(search_string = word)
            if (response.isSuccessful) {
                userList.value = response.body()!!.items
            }
        }
    }

    fun insertUser(userItem: UserItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertLocalUser(userItem)
            setLocalTargetWord(currentLocalWord)
        }
    }

    fun deleteUser(userItem: UserItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLocalUser(userItem)
            setLocalTargetWord(currentLocalWord)
        }
    }
}