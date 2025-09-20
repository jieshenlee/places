package com.example.places.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.places.data.entity.ExploreDestination
import com.example.places.data.entity.ExplorePerson
import com.example.places.databinding.FragmentPlacesBinding
import com.example.places.databinding.FragmentPeopleBinding
import com.example.places.databinding.FragmentTripsBinding

class TripsFragment : Fragment() {
    private var _binding: FragmentTripsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: ExploreDestinationAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadSampleData()
    }
    
    private fun setupRecyclerView() {
        adapter = ExploreDestinationAdapter(
            onDestinationClick = { destination ->
                // TODO: Navigate to destination detail
            },
            onBookmarkClick = { destination ->
                // TODO: Toggle bookmark
            }
        )
        
        binding.rvTrips.apply {
            adapter = this@TripsFragment.adapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }
    
    private fun loadSampleData() {
        val sampleTrips = listOf(
            ExploreDestination("1", "Machu Picchu", "Cusco Region", "Peru", ""),
            ExploreDestination("2", "Blue Mosque", "Istanbul", "Turkey", ""),
            ExploreDestination("3", "Big Buddha", "Phuket", "Thailand", ""),
            ExploreDestination("4", "Sydney Harbour", "Sydney", "Australia", ""),
            ExploreDestination("5", "Sagrada Familia", "Barcelona", "Spain", ""),
            ExploreDestination("6", "Maldives Resort", "Maldives", "Maldives", ""),
            ExploreDestination("7", "Santorini", "Santorini", "Greece", ""),
            ExploreDestination("8", "Manhattan", "New York", "USA", ""),
            ExploreDestination("9", "Tokyo Skyline", "Tokyo", "Japan", ""),
            ExploreDestination("10", "St. Peter's Basilica", "Vatican City", "Vatican", "")
        )
        adapter.submitList(sampleTrips)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PeopleFragment : Fragment() {
    private var _binding: FragmentPeopleBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: ExplorePersonAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPeopleBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadSampleData()
    }
    
    private fun setupRecyclerView() {
        adapter = ExplorePersonAdapter(
            onPersonClick = { person ->
                // TODO: Navigate to person profile
            },
            onFollowClick = { person ->
                // TODO: Toggle follow
            }
        )
        
        binding.rvPeople.apply {
            adapter = this@PeopleFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    
    private fun loadSampleData() {
        val samplePeople = listOf(
            ExplorePerson("1", "Sarah Johnson", "@sarahj", "Travel photographer • 25 countries", null, 1200, 450, 47),
            ExplorePerson("2", "Marco Rodriguez", "@marcotravel", "Adventure seeker • Mountain climber", null, 890, 320, 32),
            ExplorePerson("3", "Emma Chen", "@emmawanders", "Food & culture enthusiast", null, 2100, 680, 78),
            ExplorePerson("4", "David Kim", "@davidexplores", "Digital nomad • Tech blogger", null, 1500, 890, 56),
            ExplorePerson("5", "Lisa Thompson", "@lisaonthego", "Solo traveler • 40+ countries", null, 980, 420, 41),
            ExplorePerson("6", "Alex Rivera", "@alexadventures", "Wildlife photographer", null, 750, 290, 28)
        )
        adapter.submitList(samplePeople)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PlacesFragment : Fragment() {
    private var _binding: FragmentPlacesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var adapter: ExploreDestinationAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlacesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadSampleData()
    }
    
    private fun setupRecyclerView() {
        adapter = ExploreDestinationAdapter(
            onDestinationClick = { destination ->
                // TODO: Navigate to destination detail
            },
            onBookmarkClick = { destination ->
                // TODO: Toggle bookmark
            }
        )
        
        binding.rvPlaces.apply {
            adapter = this@PlacesFragment.adapter
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }
    
    private fun loadSampleData() {
        val samplePlaces = listOf(
            ExploreDestination("11", "Paris", "Île-de-France", "France", ""),
            ExploreDestination("12", "Bali", "Bali Province", "Indonesia", ""),
            ExploreDestination("13", "Rome", "Lazio", "Italy", ""),
            ExploreDestination("14", "Kyoto", "Kyoto Prefecture", "Japan", ""),
            ExploreDestination("15", "Cape Town", "Western Cape", "South Africa", ""),
            ExploreDestination("16", "Iceland", "Reykjavik", "Iceland", ""),
            ExploreDestination("17", "Dubai", "Dubai Emirate", "UAE", ""),
            ExploreDestination("18", "London", "England", "UK", "")
        )
        adapter.submitList(samplePlaces)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
