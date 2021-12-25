

class RoomAlreadyFilledException(val roomNumber:Int, val student:Student):Exception()
class RoomAlreadyEmptyException(val roomNumber: Int):Exception()
class AllRoomsFilledException(val student: Student): Exception()
class NoRoomsLeftException(val students: List<Student>):Exception()

enum class HostelType {
    Boys,
    Girls,
    Coed
}

abstract class Estate(val name:String)

class Mess(name:String, capacity:Int):Estate(name)

class Bhawan(name:String, val rooms:Int, val hostelType:HostelType): Estate(name){
    val filledList:MutableMap<Int, Student> = mutableMapOf()

    fun allocate(student: Student){
        if(filledList.size == rooms){
            throw AllRoomsFilledException(student)
        }
        var allocatedRoom = 0
        while(filledList.containsKey(allocatedRoom)){
            allocatedRoom = (0 until rooms).random()
        }
        fill(student, allocatedRoom)
    }

    fun fill(student:Student, room:Int){
        if(filledList[room]!=null){
            throw RoomAlreadyFilledException(room, student)
        }
        filledList[room] = student
    }

    fun vacate(room:Int){
        if(filledList[room]==null){
            throw RoomAlreadyEmptyException(room)
        }
        filledList.remove(room)
    }

}

data class Student(val id:String, val name: String, var age:Int, var preference: HostelType)

fun allocateRooms(student: List<Student>, bhawans:List<Bhawan>){
    var boysAllocated = 0
    var girlsAllocated = 0
    var coedAllocated = 0

    val boysList = student.filter { it.preference == HostelType.Boys }
    val girlsList = student.filter { it.preference == HostelType.Girls }
    val coedList = student.filter { it.preference == HostelType.Coed }

    for(bhawan in bhawans){
        when(bhawan.hostelType){
            HostelType.Boys -> {
                val remainingBoys = boysList.subList(boysAllocated, boysList.size)
                for(boy in remainingBoys){
                    try{
                        bhawan.allocate(boy)
                        boysAllocated++
                    } catch (e: AllRoomsFilledException){
                        break
                    }
                }
            }
            HostelType.Girls -> {
                val remainingGirls = girlsList.subList(girlsAllocated, girlsList.size)
                for(girl in remainingGirls){
                    try {
                        bhawan.allocate(girl)
                        girlsAllocated++
                    } catch (e: AllRoomsFilledException){
                        break
                    }
                }
            }
            HostelType.Coed -> {
                val remainingCoed = coedList.subList(coedAllocated, coedList.size)
                for(coed in remainingCoed){
                    try {
                        bhawan.allocate(coed)
                        coedAllocated++
                    } catch (e: AllRoomsFilledException){
                        break
                    }
                }
            }
        }
    }

    if(boysAllocated != boysList.size || girlsAllocated != girlsList.size || coedAllocated != coedList.size){
        throw NoRoomsLeftException(boysList.subList(boysAllocated, boysList.size)
        + girlsList.subList(girlsAllocated, girlsList.size)
        + coedList.subList(coedAllocated, coedList.size)
        )
    }
}

fun main() {
//    val ramBhawan = Bhawan("Ram", 200, HostelType.Boys)
//    println(Bhawan("Ram", 200, HostelType.Boys) == Bhawan("Ram", 200, HostelType.Boys))
//    println(Student("2019B5A80383P", "Ayush", 21, HostelType.Boys)==Student("2019B5A80383P", "Ayush", 21, HostelType.Boys))
//    println(ramBhawan.name)
//    println(ramBhawan.rooms)
//    println(ramBhawan.hostelType)
//    println(ramBhawan.filledList[1])
//    println(ramBhawan.filledList[2])
//    try {
//        ramBhawan.fill(Student("2019B5A80383P", "Ayush", 21, HostelType.Boys), 3)
//        println(ramBhawan.filledList[3])
//        ramBhawan.fill(Student("2019B4A70336P", "Rahul", 21, HostelType.Coed), 3)
//    }catch (e:RoomAlreadyFilledException){
//        println("Room ${e.roomNumber} Already Filled. Cant add ${e.student.name}")
//    }
//    ramBhawan.vacate(3)
//    println(ramBhawan.filledList[3])
    try {
        allocateRooms(studentsList, bhawansList)
        println("All students allocated successfully")
    }catch (e:NoRoomsLeftException){
        println("No rooms left for ${e.students.size} students")
    }

}

val studentsList = (0..200).map {_ ->
    Student(
        (0..9).map {
            (('a'..'z')+('A'..'Z')+('0'..'9')).random()
        }.joinToString(separator = ""),
        (0..5).map {
            (('a'..'z')).random()
        }.joinToString(separator = "", prefix = ('A'..'Z').random().toString()),
        (18..24).random(),
        HostelType.values().random()
    )
}


val bhawansList = listOf(
    Bhawan("Ram", 120, HostelType.Boys),
    Bhawan("Meera", 70, HostelType.Girls),
    Bhawan("Shankar", 70, HostelType.Coed)
)

