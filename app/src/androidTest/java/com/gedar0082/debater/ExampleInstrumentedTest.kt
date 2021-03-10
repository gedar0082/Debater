package com.gedar0082.debater

import android.content.Context
import android.provider.Settings
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gedar0082.debater.model.local.DebateDB
import com.gedar0082.debater.model.local.entity.Debate
import com.gedar0082.debater.model.local.entity.Thesis
import com.gedar0082.debater.model.local.entityDao.DebateDao
import com.gedar0082.debater.model.local.entityDao.ThesisDao
import com.gedar0082.debater.repository.DebateRepository
import com.gedar0082.debater.repository.ThesisRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException
import java.lang.Exception

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var debateDao: DebateDao
    private lateinit var thesisDao: ThesisDao
    private lateinit var debateRepo: DebateRepository
    private lateinit var thesisRepo: ThesisRepository
    private lateinit var db : DebateDB

    @Before
    fun createDB(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, DebateDB::class.java).build()
        debateDao = db.debateDao()
        thesisDao = db.thesisDao()
        debateRepo = DebateRepository(debateDao)
        thesisRepo = ThesisRepository(thesisDao)
    }

    @After
    @Throws(IOException::class)
    fun closeDB(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertDebate(){
        val debate = Debate(0, "lalala", "a")


        GlobalScope.launch {
            debateRepo.insert(debate)
            println(debateRepo.debates.value?.size ?: 100)
            assertEquals(debateRepo.debates.value?.size, 1)
        }
    }

    @Test
    @Throws(Exception::class)
    fun insertThesis(){
        val debate = Debate(0, "lalala", "a")

        val thesis1 = Thesis(0, 0, "a", "b")
        val thesis2 = Thesis(0, 0, "a1", "b1")

        GlobalScope.launch {
            debateRepo.insert(debate)

            thesisRepo.insert(thesis1)
            thesisRepo.insert(thesis2)


        }
        assertEquals(thesisRepo.getAllTheses().value?.size, 2)
        assertEquals(thesisRepo.getThesesFromDebateId(0), 2)
        assertEquals(thesisRepo.getThesesFromDebateId(12311), 2)


    }



}