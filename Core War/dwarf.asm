; the dwarf
loop	ADD #4, bomb
     MOV bomb, @bomb
     JMP loop
bomb	DAT #0, #0
