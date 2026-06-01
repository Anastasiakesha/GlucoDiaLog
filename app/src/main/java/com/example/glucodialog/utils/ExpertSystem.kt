package com.example.glucodialog.utils

import com.example.glucodialog.domain.model.*
import java.util.Calendar

class Facts {
    @PublishedApi
    internal val data = mutableMapOf<String, Any>()

    fun put(key: String, value: Any) { data[key] = value }
    inline fun <reified T> get(key: String): T? = data[key] as? T
}

interface Rule {
    val name: String
    val priority: Int
    fun evaluate(facts: Facts): Boolean
    fun execute(facts: Facts): String
}

class RuleEngine {
    private val rules = mutableListOf<Rule>()

    fun registerRule(rule: Rule) {
        rules.add(rule)
        rules.sortByDescending { it.priority }
    }

    fun fire(facts: Facts): List<String> {
        val triggeredAdvices = mutableListOf<String>()
        for (rule in rules) {
            if (rule.evaluate(facts)) triggeredAdvices.add(rule.execute(facts))
        }
        return triggeredAdvices
    }
}

fun Long.isBetweenHours(startHour: Int, endHour: Int): Boolean {
    val cal = Calendar.getInstance().apply { timeInMillis = this@isBetweenHours }
    val h = cal.get(Calendar.HOUR_OF_DAY)
    return h in startHour..endHour
}

// 1. Нефропатия / Сердечно-сосудистый риск
class NephropathyRiskRule : Rule {
    override val name = "Риск нефропатии"
    override val priority = 150
    override fun evaluate(facts: Facts): Boolean {
        val profile = facts.get<UserProfile>("profile") ?: return false
        val bps = facts.get<List<BloodPressureEntry>>("bp") ?: emptyList()
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: emptyList()
        if (profile.diabetesType != "2 type" || bps.isEmpty() || glucoses.isEmpty()) return false

        val avgSys = bps.takeLast(5).map { it.systolic }.average()
        val avgGlu = glucoses.takeLast(14).map { if (it.unit == "мг/дл") it.glucoseLevel/18 else it.glucoseLevel }.average()
        return avgSys > 140 && avgGlu > 9.0
    }
    override fun execute(facts: Facts): String = "Хронически высокий сахар (>9.0) в сочетании с гипертонией (АД > 140) резко повышает риск поражения почек (нефропатии) и инфаркта. Срочно обратитесь к кардиологу и эндокринологу."
}

// 2. Преэклампсия (ГСД)
class PreeclampsiaRule : Rule {
    override val name = "Риск преэклампсии"
    override val priority = 145
    override fun evaluate(facts: Facts): Boolean {
        val profile = facts.get<UserProfile>("profile") ?: return false
        val bps = facts.get<List<BloodPressureEntry>>("bp") ?: emptyList()
        if (profile.diabetesType != "gestational" || bps.isEmpty()) return false
        return bps.last().systolic > 130 || bps.last().diastolic > 85
    }
    override fun execute(facts: Facts): String = "При гестационном диабете повышение АД (>130/85) может быть признаком преэклампсии — опасного состояния для плода. Немедленно свяжитесь с акушером-гинекологом."
}

// 3. Эффект Сомоджи (Ночная гипо -> Утренняя гипер)
class SomogyiEffectRule : Rule {
    override val name = "Эффект Сомоджи"
    override val priority = 140
    override fun evaluate(facts: Facts): Boolean {
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: return false
        val last24h = glucoses.filter { System.currentTimeMillis() - it.timestamp < 86400000 }
        val nightHypo = last24h.any { it.timestamp.isBetweenHours(2, 4) && it.glucoseLevel < 3.9 }
        val morningHyper = last24h.any { it.timestamp.isBetweenHours(6, 9) && it.glucoseLevel > 8.0 }
        return nightHypo && morningHyper
    }
    override fun execute(facts: Facts): String = "Утренняя гипергликемия вызвана скрытой ночной гипогликемией (эффект Сомоджи). Организм выбросил гормоны стресса. Не увеличивайте утренний инсулин! Снизьте вечернюю базу."
}

// 4. Синдром "Утренней зари"
class DawnPhenomenonRule : Rule {
    override val name = "Утренняя заря"
    override val priority = 135
    override fun evaluate(facts: Facts): Boolean {
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: return false
        val last24h = glucoses.filter { System.currentTimeMillis() - it.timestamp < 86400000 }
        val nightNormal = last24h.any { it.timestamp.isBetweenHours(0, 4) && it.glucoseLevel in 4.5..7.0 }
        val morningHyper = last24h.any { it.timestamp.isBetweenHours(6, 9) && it.glucoseLevel > 8.0 }
        return nightNormal && morningHyper
    }
    override fun execute(facts: Facts): String = "Синдром «утренней зари»: сахар ночью в норме, но растет к утру из-за выброса кортизола. Обсудите с врачом корректировку времени или дозы продленного инсулина."
}

// 5. Диабетический гастропарез (Задержка всасывания)
class GastroparesisRule : Rule {
    override val name = "Гастропарез"
    override val priority = 130
    override fun evaluate(facts: Facts): Boolean {
        val meals = facts.get<List<FoodEntryWithTypeDomain>>("meals") ?: return false
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: return false
        if (meals.isEmpty() || glucoses.isEmpty()) return false

        val lastMeal = meals.maxByOrNull { it.entry.timestamp } ?: return false
        val post1h = glucoses.find { it.timestamp > lastMeal.entry.timestamp && it.timestamp < lastMeal.entry.timestamp + 3600000 }
        val post4h = glucoses.find { it.timestamp > lastMeal.entry.timestamp + 10800000 && it.timestamp < lastMeal.entry.timestamp + 18000000 }

        return (post1h?.glucoseLevel ?: 5.0) < 4.5 && (post4h?.glucoseLevel ?: 5.0) > 10.0
    }
    override fun execute(facts: Facts): String = "Гипогликемия сразу после еды с мощным скачком сахара через 3-4 часа указывает на замедленное опорожнение желудка (гастропарез). Инсулин обгоняет еду. Рассмотрите введение болюса ПОСЛЕ еды."
}

// 6. Выраженная инсулинорезистентность
class SevereInsulinResistanceRule : Rule {
    override val name = "Инсулинорезистентность"
    override val priority = 125
    override fun evaluate(facts: Facts): Boolean {
        val profile = facts.get<UserProfile>("profile") ?: return false
        val insulins = facts.get<List<InsulinEntryWithTypeDomain>>("insulin") ?: return false
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: return false

        if (profile.diabetesType != "2 type" || profile.weight == 0.0 || profile.height == 0.0) return false
        val bmi = profile.weight / Math.pow(profile.height / 100.0, 2.0)

        val last24hInsulin = insulins.filter { System.currentTimeMillis() - it.entry.timestamp < 86400000 }.sumOf { it.entry.doseUnits }
        val avgGlu = glucoses.takeLast(10).map { it.glucoseLevel }.average()

        return bmi > 30.0 && (last24hInsulin / profile.weight) > 1.0 && avgGlu > 8.5
    }
    override fun execute(facts: Facts): String = "Выраженная инсулинорезистентность. Несмотря на высокие дозы инсулина (>1 ЕД/кг), сахар высокий. Избыточный вес (ИМТ > 30) блокирует работу инсулина. Необходим пересмотр терапии."
}

// 7. Синдром нераспознавания гипогликемии
class HypoUnawarenessRule : Rule {
    override val name = "Нераспознавание гипо"
    override val priority = 120
    override fun evaluate(facts: Facts): Boolean {
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: return false
        val hypos48h = glucoses.filter { System.currentTimeMillis() - it.timestamp < 48 * 3600000 && it.glucoseLevel < 3.5 }
        return hypos48h.size >= 3
    }
    override fun execute(facts: Facts): String = "Зафиксировано более трех тяжелых падений сахара за 48 часов! Это ведет к синдрому нераспознавания гипогликемии (вы перестанете чувствовать симптомы). Целевые сахара нужно временно поднять до 6-8 ммоль/л."
}

// 8. Эффект пиццы (БЖУ-скачок)
class PizzaEffectRule : Rule {
    override val name = "Эффект пиццы"
    override val priority = 115
    override fun evaluate(facts: Facts): Boolean {
        val meals = facts.get<List<FoodEntryWithTypeDomain>>("meals") ?: return false
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: return false

        val heavyMeal = meals.find {
            val f = it.type?.fats ?: 0.0; val p = it.type?.proteins ?: 0.0
            (f * (it.entry.quantity/100) > 30) && (p * (it.entry.quantity/100) > 40) && (System.currentTimeMillis() - it.entry.timestamp < 6 * 3600000)
        } ?: return false

        val lateGlu = glucoses.find { it.timestamp > heavyMeal.entry.timestamp + 4 * 3600000 }
        return (lateGlu?.glucoseLevel ?: 0.0) > 10.0
    }
    override fun execute(facts: Facts): String = "«Эффект пиццы»: белково-жировая пища замедлила всасывание и конвертировалась в глюкозу через 5 часов. Зафиксирован отложенный скачок. Дробите дозу инсулина на 2 части при такой еде."
}

// 9. Отсроченная гипо после тренировки
class DelayedExerciseHypoRule : Rule {
    override val name = "Отсроченная гипо"
    override val priority = 110
    override fun evaluate(facts: Facts): Boolean {
        val activities = facts.get<List<ActivityEntryWithTypeDomain>>("activity") ?: return false
        val eveningActivity = activities.find { it.entry.timestamp.isBetweenHours(17, 22) && it.entry.durationMinutes >= 60 }
        return eveningActivity != null && (System.currentTimeMillis() - eveningActivity.entry.timestamp < 12 * 3600000)
    }
    override fun execute(facts: Facts): String = "Длительная вечерняя тренировка истощила запасы гликогена в печени. Это грозит тяжелой гипогликемией ночью! Съешьте медленные углеводы перед сном или снизьте ночную базу."
}

// 10. Адреналиновый скачок
class AdrenalineSpikeRule : Rule {
    override val name = "Адреналиновый скачок"
    override val priority = 105
    override fun evaluate(facts: Facts): Boolean {
        val activities = facts.get<List<ActivityEntryWithTypeDomain>>("activity") ?: return false
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: return false

        val shortIntense = activities.find { it.entry.durationMinutes < 30 && (System.currentTimeMillis() - it.entry.timestamp < 2 * 3600000) } ?: return false
        val postActivityGlu = glucoses.find { it.timestamp > shortIntense.entry.timestamp }
        return (postActivityGlu?.glucoseLevel ?: 0.0) > 10.0
    }
    override fun execute(facts: Facts): String = "Короткая интенсивная нагрузка вызвала выброс адреналина и рост глюкозы. Не скалывайте этот сахар большими дозами инсулина! Он резко упадет сам после распада гормонов стресса."
}

// 11. Неверный углеводный коэффициент (УК)
class IncorrectICRRule : Rule {
    override val name = "Неверный УК"
    override val priority = 100
    override fun evaluate(facts: Facts): Boolean {
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: return false
        val meals = facts.get<List<FoodEntryWithTypeDomain>>("meals") ?: return false

        val fasting = glucoses.filter { it.timestamp.isBetweenHours(6, 9) }.takeLast(3).all { it.glucoseLevel in 4.0..6.5 }
        if (!fasting) return false

        var postMealSpikes = 0
        meals.takeLast(5).forEach { meal ->
            val spike = glucoses.find { it.timestamp > meal.entry.timestamp && it.timestamp < meal.entry.timestamp + 7200000 }
            if ((spike?.glucoseLevel ?: 0.0) > 10.0) postMealSpikes++
        }
        return postMealSpikes >= 3
    }
    override fun execute(facts: Facts): String = "Ваш тощаковый сахар идеален, но после еды всегда стабильно высокий. Вероятно, ваш углеводный коэффициент (УК) рассчитан неверно или вы делаете недостаточную паузу перед едой."
}

// 12. Риск макросомии (ГСД)
class GestationalMacrosomiaRule : Rule {
    override val name = "Макросомия ГСД"
    override val priority = 95
    override fun evaluate(facts: Facts): Boolean {
        val profile = facts.get<UserProfile>("profile") ?: return false
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: return false
        val meals = facts.get<List<FoodEntryWithTypeDomain>>("meals") ?: return false

        if (profile.diabetesType != "gestational") return false
        val lastMeal = meals.maxByOrNull { it.entry.timestamp } ?: return false
        val post1h = glucoses.find { it.timestamp > lastMeal.entry.timestamp && it.timestamp < lastMeal.entry.timestamp + 5400000 }

        return (post1h?.glucoseLevel ?: 0.0) > 7.0
    }
    override fun execute(facts: Facts): String = "При гестационном диабете сахар через час после еды > 7.0 ммоль/л грозит макросомией (крупным плодом). Исключите быстрые углеводы и добавьте клетчатку в этот прием пищи."
}

// 13. Лактатацидоз (Метформин)
class LacticAcidosisRule : Rule {
    override val name = "Лактатацидоз"
    override val priority = 90
    override fun evaluate(facts: Facts): Boolean {
        val meds = facts.get<List<MedicationEntryWithTypeDomain>>("meds") ?: return false
        val activities = facts.get<List<ActivityEntryWithTypeDomain>>("activity") ?: return false

        val takesMetformin = meds.any { it.type?.name?.contains("Метформин", true) == true || it.type?.name?.contains("Глюкофаж", true) == true }
        val heavyActivity = activities.any { it.entry.durationMinutes > 90 && (System.currentTimeMillis() - it.entry.timestamp < 12 * 3600000) }

        return takesMetformin && heavyActivity
    }
    override fun execute(facts: Facts): String = "Тяжелые физнагрузки на фоне приема метформина повышают риск накопления молочной кислоты (лактатацидоз). Обязательно пейте много чистой воды для защиты почек!"
}

// 14. Скрытая инфекция (Sick Day)
class SickDayRule : Rule {
    override val name = "Дни болезни"
    override val priority = 85
    override fun evaluate(facts: Facts): Boolean {
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: return false
        val last24h = glucoses.filter { System.currentTimeMillis() - it.timestamp < 86400000 }
        return last24h.size >= 3 && last24h.all { it.glucoseLevel > 11.0 }
    }
    override fun execute(facts: Facts): String = "Сахар держится выше 11.0 ммоль/л более суток! Это классический признак скрытого воспаления, простуды или инфекции. Потребность в инсулине в «дни болезни» возрастает на 20-50%. Проверьте кетоны."
}

// 15. Липогипертрофия
class LipohypertrophyRule : Rule {
    override val name = "Липогипертрофия"
    override val priority = 80
    override fun evaluate(facts: Facts): Boolean {
        val insulins = facts.get<List<InsulinEntryWithTypeDomain>>("insulin") ?: return false
        val glucoses = facts.get<List<GlucoseEntry>>("glucose") ?: return false

        val last3DaysInsulin = insulins.filter { System.currentTimeMillis() - it.entry.timestamp < 3 * 86400000 }.sumOf { it.entry.doseUnits } / 3
        val prev3DaysInsulin = insulins.filter { System.currentTimeMillis() - it.entry.timestamp in (3 * 86400000)..(6 * 86400000) }.sumOf { it.entry.doseUnits } / 3
        val avgGlu = glucoses.takeLast(10).map { it.glucoseLevel }.average()

        return (last3DaysInsulin > prev3DaysInsulin * 1.3) && avgGlu > 10.0
    }
    override fun execute(facts: Facts): String = "Потребность в инсулине резко выросла на 30%, но сахара остаются высокими. Проверьте места инъекций на наличие уплотнений («шишек»). Инсулин из них не всасывается. Смените зону уколов."
}