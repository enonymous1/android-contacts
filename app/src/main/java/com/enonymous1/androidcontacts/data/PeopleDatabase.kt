package com.enonymous1.androidcontacts.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.enonymous1.androidcontacts.model.Person
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** One row per person. The id is the identity used for details and delete. */
@Entity(tableName = "people")
data class PersonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val dob: String,
) {
    fun toDomain(): Person = Person(id, name, phone, dob)

    companion object {
        fun fromDomain(person: Person): PersonEntity = PersonEntity(
            id = person.id,
            name = person.name,
            phone = person.phone,
            dob = person.dob,
        )
    }
}

@Dao
interface PeopleDao {
    @Query("SELECT * FROM people ORDER BY name COLLATE NOCASE ASC, id ASC")
    fun observeAll(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM people WHERE id = :id")
    fun observeById(id: Long): Flow<PersonEntity?>

    @Insert
    suspend fun insert(person: PersonEntity)

    @Delete
    suspend fun delete(person: PersonEntity)
}

@Database(entities = [PersonEntity::class], version = 1, exportSchema = false)
abstract class PeopleDatabase : RoomDatabase() {
    abstract fun peopleDao(): PeopleDao
}

/** The data layer behind [com.enonymous1.androidcontacts.PeopleViewModel]. Kept as an interface so tests can fake it. */
interface PeopleRepository {
    val people: Flow<List<Person>>
    fun observePerson(id: Long): Flow<Person?>
    suspend fun add(person: Person)
    suspend fun delete(person: Person)
}

class RoomPeopleRepository(private val dao: PeopleDao) : PeopleRepository {
    override val people: Flow<List<Person>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observePerson(id: Long): Flow<Person?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun add(person: Person) {
        dao.insert(PersonEntity.fromDomain(person))
    }

    override suspend fun delete(person: Person) {
        dao.delete(PersonEntity.fromDomain(person))
    }
}
