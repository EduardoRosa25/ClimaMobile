/** Deepseek - início
 * Prompt: criar funções para inserir, deletar a última das 3 cidades e buscar uma cidade por nome
 */
package com.example.apptrabalho2_metereologia.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: CityEntity)

    @Query("SELECT * FROM city_history ORDER BY id DESC LIMIT 3")
    fun getLastThreeCities(): Flow<List<CityEntity>>

    @Query("DELETE FROM city_history WHERE id NOT IN (SELECT id FROM city_history ORDER BY id DESC LIMIT 3)")
    suspend fun deleteOldCities()

    @Query("SELECT * FROM city_history WHERE LOWER(cityName) = LOWER(:cityName) LIMIT 1")
    suspend fun findCityByName(cityName: String): CityEntity?
}
/** Deepseek - final */