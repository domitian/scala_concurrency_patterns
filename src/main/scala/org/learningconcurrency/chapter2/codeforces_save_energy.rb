k, d, t = gets.chomp.split(" ").map {|k| k.to_i}
i = 0
while i <= t do
    i+=k
    if i < t
        r = i%d
        if i < d
        	r = d-i
        end
        t += (r/2.0)
    end
end
puts t