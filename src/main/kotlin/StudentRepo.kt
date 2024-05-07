import java.sql.Connection
import java.sql.SQLException
import java.sql.Statement

class StudentRepo :IRepo {
    override fun getAllStudents(): Result<List<String>> {
        var connectionDb: Connection? = null
        var stmt: Statement? = null

        try {
            val students = mutableListOf<String>()

            connectionDb = Database.getConnection()
            stmt = connectionDb.createStatement()

            val query = "SELECT name FROM students"
            val rs = stmt.executeQuery(query)

            while (rs.next()) {
                students.add(rs.getString("name"))
            }

            stmt.close()
            connectionDb.close()
            return Result.success(students)

        } catch (e: Exception) {
            stmt?.close()
            connectionDb?.close()
            return Result.failure(e)
        }
    }


    override fun updateStudents(students: List<String>): Result<Unit> {
        var connectionDb : Connection? = null
        var stmt:Statement? = null
        var error:Exception? = null
        try {
            connectionDb = Database.getConnection()
            connectionDb.autoCommit = false

            stmt = connectionDb.createStatement()
            val query = "DELETE FROM students"
            stmt.execute(query)

            stmt = connectionDb.prepareStatement("INSERT INTO students (name) VALUES (?)")
                for (student in students) {
                    stmt.setString(1, student)
                    stmt.executeUpdate()
                }



            Result.success(Unit)
        } catch (e: Exception) {
            error = e
            try {
                connectionDb?.rollback()
            }catch (e:SQLException) {
                error = e
            }

        } finally {
            connectionDb?.autoCommit = true
            stmt?.close()
            connectionDb?.close()
        }

        if (error != null) {
            return Result.failure(error)
        }
        return Result.success(Unit)
    }

}