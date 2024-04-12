package com.example.panguproject.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.panguproject.model.GameState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


val Context.dataStore: DataStore<Preferences> by preferencesDataStore("game_data.pb")

class GameStateStorage(context: Context) {
    private val dataStore = context.dataStore

    @Suppress("PrivatePropertyName")
    private val SAVED_STATE = stringPreferencesKey("saved_state")

    suspend fun clearState() {
        dataStore.edit { storage ->
            storage.remove(SAVED_STATE)
        }
    }

    fun hasSavedState(): Boolean {
        return runBlocking {
            dataStore.data.map { it[SAVED_STATE] }.first() != null
        }
    }

    suspend fun saveState(state: GameState) {
        val unselectedState = state.copy(
            diceList = state.diceList.map { it.copy(selected = false) },
        )
        dataStore.edit { storage ->
            storage[SAVED_STATE] = Json.encodeToString(GameState.serializer(), unselectedState)
        }
    }

    fun loadState(): GameState? {
        val serializedGameState = runBlocking {
            dataStore.data.map { it[SAVED_STATE] }.first()
        }
        return serializedGameState?.let { Json.decodeFromString(GameState.serializer(), it) }
    }
}