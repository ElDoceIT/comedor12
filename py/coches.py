class Coche:
    def __init__(self, marca, color):
        self.marca= marca
        self.color= color
    
    def confucir(self):
        print("voy manejando")

    def mantenimiento(self):
        if self.marca=="Toyota":
            print("mantenimiento cada 5")
        else:
            print("cada 3 años")

micoche=Coche("Toyota", "Rojo")
micoche.mantenimiento()